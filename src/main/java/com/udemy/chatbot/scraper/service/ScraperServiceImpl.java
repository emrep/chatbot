package com.udemy.chatbot.scraper.service;

import com.udemy.chatbot.scraper.apicaller.ApiCallQueue;
import com.udemy.chatbot.scraper.apicaller.ApiCaller;
import com.udemy.chatbot.scraper.dao.ScraperRepository;
import com.udemy.chatbot.scraper.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Service
public class ScraperServiceImpl implements ScraperService {
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

    @Value("${api.caller.thread.sleep.time}")
    private long threadSleepTime;

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
    public void scrapeContent() {
        scraperRepository.dropCollection();
        List<CourseType> categoryList  = getCategories();

        categoryList.forEach(category -> categoryQueue.addQueue(() -> this.getSubCategories(category)));
        List<CourseType> subCategoryList = apiCaller.getCategories(categoryQueue);

        subCategoryList.forEach(subcategory -> subcategoryQueue.addQueue(() -> this.getTopics(subcategory)));
        List<CourseType> topicList = apiCaller.getCategories(subcategoryQueue);

        topicList.forEach(topic -> topicQueue.addQueue(() -> this.saveFirstCoursePage(topic)));
        List<Pagination> coursePageList = apiCaller.saveFirstCoursePage(topicQueue);

        coursePageList.forEach(coursePage -> coursePageQueue.addQueue(() -> this.saveOtherCoursePages(coursePage)));
        apiCaller.saveOtherCoursePages(coursePageQueue);
    }

    private List<CourseType> getCategories() {
        return restTemplate.getForObject(COURSE_CATEGORIES_URL, CourseTypeList.class).getResults();
    }

    private List<CourseType> getSubCategories(CourseType category) throws InterruptedException {
        Thread.sleep(threadSleepTime);
        List<CourseType> subCategoryList = restTemplate.getForObject(COURSE_CATEGORIES_URL + URL_DELIMETER + category.getId() + SUBCATEGORIES, CourseTypeList.class).getResults();
        subCategoryList.forEach(subCategory -> {
            subCategory.setCategory(category.getTitle());
            subCategory.setSubCategory(subCategory.getTitle());
        });
        return subCategoryList;
    }

    private List<CourseType> getTopics(CourseType subcategory) throws InterruptedException {
        Thread.sleep(threadSleepTime);
        List<CourseType> topicList = restTemplate.getForObject(SUBCATEGORIES_URL + URL_DELIMETER + subcategory.getId() + LABELS, CourseTypeList.class).getResults();
        topicList.forEach(topic -> {
            topic.setCategory(subcategory.getCategory());
            topic.setSubCategory(subcategory.getSubCategory());
            topic.setTopic(topic.getTitle());
        });
        return topicList;
    }

    private Pagination saveFirstCoursePage(CourseType topic) throws InterruptedException {
        Thread.sleep(threadSleepTime);
        CourseList courseList = restTemplate.getForObject(COURSE_URL + topic.getId() + COURSE_FILTER + pageSize, CourseList.class);
        saveCourses(topic, courseList);
        Pagination pagination = courseList.getUnit().getPagination();
        pagination.setTopic(topic);
        return pagination;
    }

    private Boolean saveOtherCoursePages(Pagination pagination) throws InterruptedException {
        Thread.sleep(threadSleepTime);
        CourseType topic = pagination.getTopic();
        while(Objects.nonNull(pagination.getNext())) {
            CourseList courseList = restTemplate.getForObject(UDEMY_URL + pagination.getNext().getUrl(), CourseList.class);
            saveCourses(topic, courseList);
            pagination = courseList.getUnit().getPagination();
        }
        return true;
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
