package com.udemy.chatbot.scraper.service;

import com.udemy.chatbot.scraper.apicaller.ApiCallQueue;
import com.udemy.chatbot.scraper.apicaller.ApiCaller;
import com.udemy.chatbot.scraper.dao.ScraperRepository;
import com.udemy.chatbot.scraper.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class ScraperServiceImpl implements ScraperService {

    private static final Logger log = LoggerFactory.getLogger(ScraperService.class);

    private static final String UDEMY_URL = "https://www.udemy.com";
    private static final String UDEMY_API_URL = UDEMY_URL + "/api-2.0";
    private static final String COURSE_CATEGORIES_URL = UDEMY_API_URL + "/course-categories";
    private static final String SUBCATEGORIES_URL = UDEMY_API_URL + "/course-subcategories";
    private static final String COURSE_URL = UDEMY_API_URL + "/discovery-units/all_courses/?source_page=topic_page&label_id=";
    private static final String SUBCATEGORIES = "/subcategories";
    private static final String LABELS = "/labels";
    private static final String COURSE_FILTER = "&fl=lbl&sos=pl&p=1&page_size=";
    private static final String URL_DELIMETER = "/";

    @Value("${scraper.page.size}")
    private String pageSize;
    @Value("${limited.data}")
    private boolean limitedData;
    @Value("${api.caller.thread.sleep.time}")
    private long threadSleepTime;

    private EnumScrapingState scrapingState = EnumScrapingState.NOT_STARTED;

    private final RestTemplate restTemplate;
    private final ScraperRepository scraperRepository;
    private final ApiCallQueue<List<CourseType>> categoryQueue;
    private final ApiCallQueue<List<CourseType>> subcategoryQueue;
    private final ApiCallQueue<Pagination> topicQueue;
    private final ApiCallQueue<Boolean> coursePageQueue;
    private final ApiCaller apiCaller;

    @Autowired
    public ScraperServiceImpl(RestTemplate restTemplate, ScraperRepository scraperRepository, ApiCallQueue<List<CourseType>> categoryQueue, ApiCallQueue<List<CourseType>> subcategoryQueue, ApiCallQueue<Pagination> topicQueue, ApiCallQueue<Boolean> coursePageQueue, ApiCaller apiCaller) {
        this.restTemplate = restTemplate;
        this.scraperRepository = scraperRepository;
        this.categoryQueue = categoryQueue;
        this.subcategoryQueue = subcategoryQueue;
        this.topicQueue = topicQueue;
        this.coursePageQueue = coursePageQueue;
        this.apiCaller = apiCaller;
    }

    @Override
    public void bootContentScraper() {
        // TODO: 22-Mar-19 Scheduling a 'Retry Scraping Task'
        while(!scrapingState.equals(EnumScrapingState.COMPLETED)) {
            if(scrapingState.equals(EnumScrapingState.NOT_STARTED) || scrapingState.equals(EnumScrapingState.FAILED)) {
                scrapeContent();
            } else if(scrapingState.equals(EnumScrapingState.PARTIALLY_COMPLETED)) {
                retryFailedScrapingRequests();
            }
        }
    }

    private void scrapeContent() {
        scrapingState = EnumScrapingState.IN_PROGRESS;
        scraperRepository.dropCollection();

        List<CourseType> categoryList = scrapeCategories();

        if(categoryList.isEmpty()) {
            scrapingState = EnumScrapingState.FAILED;
            return;
        }
        scrapeCourses();
    }

    private void retryFailedScrapingRequests() {
        scrapingState = EnumScrapingState.RETRYING;
        categoryQueue.moveFailedToQueued();
        subcategoryQueue.moveFailedToQueued();
        topicQueue.moveFailedToQueued();
        coursePageQueue.moveFailedToQueued();
        scrapeCourses();
    }

    @Override
    public EnumScrapingState getScrapingState() {
        return scrapingState;
    }

    @Override
    public void setScrapingState(EnumScrapingState scrapingState) {
        this.scrapingState = scrapingState;
    }

    private List<CourseType> scrapeCategories() {
        List<CourseType> categoryList  = getCategories();
        categoryList.forEach(category -> categoryQueue.addQueue(() -> this.getSubCategories(category)));
        return categoryList;
    }

    private void scrapeCourses() {
        List<List<CourseType>> subCategoryList = apiCaller.run(categoryQueue, true);

        fillSubcategoryQueue(subCategoryList);
        List<List<CourseType>> topicList = apiCaller.run(subcategoryQueue, true);

        fillTopicQueue(topicList);
        List<Pagination> coursePageList = apiCaller.run(topicQueue, true);

        if(!limitedData) {
            fillCoursePageQueue(coursePageList);
            apiCaller.run(coursePageQueue, false);
        }

        boolean isSuccessful = categoryQueue.isFailedQueueEmpty() && subcategoryQueue.isFailedQueueEmpty() && topicQueue.isFailedQueueEmpty() && coursePageQueue.isFailedQueueEmpty();

        if(isSuccessful){
            scrapingState = EnumScrapingState.COMPLETED;
            log.info("Scraping content is completed");
        } else {
            scrapingState = EnumScrapingState.PARTIALLY_COMPLETED;
        }
    }

    private void fillSubcategoryQueue(List<List<CourseType>> subCategoryList) {
        subCategoryList.forEach(subcategories -> {
            if(limitedData && !subcategories.isEmpty()) {
                subcategoryQueue.addQueue(() -> this.getTopics(subcategories.get(0)));
            } else {
                subcategories.forEach(subcategory -> subcategoryQueue.addQueue(() -> this.getTopics(subcategory)));
            }
        });
    }

    private void fillTopicQueue(List<List<CourseType>> topicList) {
        topicList.forEach(topics -> {
            if(limitedData && !topics.isEmpty()) {
                topicQueue.addQueue(() -> this.saveFirstCoursePage(topics.get(0)));
            } else {
                topics.forEach(topic -> topicQueue.addQueue(() -> this.saveFirstCoursePage(topic)));
            }
        });
    }

    private void fillCoursePageQueue(List<Pagination> coursePageList) {
        coursePageList.forEach(coursePage -> {
            for(int pageNumber = coursePage.getCurrentPage() + 1; pageNumber <= coursePage.getTotalPage(); pageNumber++ ) {
                Pagination pagination = new Pagination();
                pagination.setTopic(coursePage.getTopic());
                Next next = new Next();
                String url = coursePage.getNext().getUrl();
                String []urlParts = url.split("&p=");
                next.setUrl(urlParts[0] + "&p=" + pageNumber + "&page_size=" + pageSize);
                pagination.setNext(next);
                coursePageQueue.addQueue(() -> this.saveNextCoursePage(pagination));
            }
        });
    }

    private List<CourseType> getCategories() {
        return requestCategoryApi(COURSE_CATEGORIES_URL);
    }

    private List<CourseType> getSubCategories(CourseType category) {
        String url = COURSE_CATEGORIES_URL + URL_DELIMETER + category.getId() + SUBCATEGORIES;
        List<CourseType> subCategoryList = requestCategoryApi(url);
        subCategoryList.forEach(subCategory -> {
            subCategory.setCategory(category.getTitle());
            subCategory.setSubCategory(subCategory.getTitle());
        });
        return subCategoryList;
    }

    private List<CourseType> getTopics(CourseType subcategory) {
        String url = SUBCATEGORIES_URL + URL_DELIMETER + subcategory.getId() + LABELS;
        List<CourseType> topicList = requestCategoryApi(url);
        topicList.forEach(topic -> {
            topic.setCategory(subcategory.getCategory());
            topic.setSubCategory(subcategory.getSubCategory());
            topic.setTopic(topic.getTitle());
        });
        return topicList;
    }

    private Pagination saveFirstCoursePage(CourseType topic) {
        String url = COURSE_URL + topic.getId() + COURSE_FILTER + pageSize;
        CourseList courseList = requestCourseApi(url);
        saveCourses(topic, courseList);
        Pagination pagination = courseList.getUnit().getPagination();
        pagination.setTopic(topic);
        return pagination;
    }

    private Boolean saveNextCoursePage(Pagination pagination) {
        CourseType topic = pagination.getTopic();
        String url = UDEMY_URL + pagination.getNext().getUrl();
        CourseList courseList = requestCourseApi(url);
        saveCourses(topic, courseList);
        return true;
    }

    private List<CourseType> requestCategoryApi(String url) {
        List<CourseType> result = restTemplate.getForObject(url, CourseTypeList.class).getResults();
        log.info("Requested URL: {}", url);
        return Objects.nonNull(result) ? result : Collections.emptyList();
    }

    private CourseList requestCourseApi(String url) {
        try {
            Thread.sleep(threadSleepTime);
        } catch (InterruptedException e) {
            log.warn("Interrupted!", e);
            Thread.currentThread().interrupt();
        }
        CourseList courseList = restTemplate.getForObject(url, CourseList.class);
        log.info("Requested URL: {}", url);
        return courseList;
    }

    private void saveCourses(CourseType topic, CourseList courseList) {
        List<Course> courses = courseList.getUnit().getItems();
        setCategoryInfo(courses, topic);
        scraperRepository.saveAll(courseList.getUnit().getItems());
    }

    private void setCategoryInfo(List<Course> courseList, CourseType topic) {
        courseList.forEach(course -> {
            course.setCategory(topic.getCategory());
            course.setSubcategory(topic.getSubCategory());
            course.setTopic(topic.getTopic());
        });

    }
}
