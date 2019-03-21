package com.udemy.chatbot.scraper.service;

import com.udemy.chatbot.scraper.dao.ScraperRepository;
import com.udemy.chatbot.scraper.model.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final String PAGE_SIZE = "20";
    private static final String URL_DELIMETER = "/";

    private final RestTemplate restTemplate;
    private final ScraperRepository scraperRepository;

    @Autowired
    public ScraperServiceImpl(RestTemplate restTemplate, ScraperRepository scraperRepository) {
        this.restTemplate = restTemplate;
        this.scraperRepository = scraperRepository;
    }

    @Override
    public void scrapeContent() {
        List<Category> categoryList  = getCategories();
        for (Category category : categoryList) {
            List<Category> subCategoryList  = getSubCategories(category);
            for (Category subcategory : subCategoryList) {
                List<Category> topicList = getTopics(subcategory);
                for (Category topic : topicList) {
                    Pagination pagination = saveFirstCoursePage(category, subcategory, topic);
                    //saveOtherCoursePages(pagination, category, subcategory, topic);
                    break;
                }
                break;
            }
            break;
        }
    }

    private List<Category> getCategories() {
        return restTemplate.getForObject(COURSE_CATEGORIES_URL, CategoryList.class).getResults();
    }

    private List<Category> getSubCategories(Category category) {
        return restTemplate.getForObject(COURSE_CATEGORIES_URL + URL_DELIMETER + category.getId() + SUBCATEGORIES, CategoryList.class).getResults();
    }

    private List<Category> getTopics(Category subcategory) {
        return restTemplate.getForObject(SUBCATEGORIES_URL + URL_DELIMETER + subcategory.getId() + LABELS, CategoryList.class).getResults();
    }

    private Pagination saveFirstCoursePage(Category category, Category subcategory, Category topic) {
        CourseList courseList = restTemplate.getForObject(COURSE_URL + topic.getId() + COURSE_FILTER + PAGE_SIZE, CourseList.class);
        saveCourses(category, subcategory, topic, courseList);
        return courseList.getUnit().getPagination();
    }

    private void saveOtherCoursePages(Pagination pagination, Category category, Category subcategory, Category topic) {
        while(Objects.nonNull(pagination.getNext())) {
            CourseList courseList = restTemplate.getForObject(UDEMY_URL + pagination.getNext().getUrl(), CourseList.class);
            saveCourses(category, subcategory, topic, courseList);
            pagination = courseList.getUnit().getPagination();
        }
    }

    private void saveCourses(Category category, Category subcategory, Category topic, CourseList courseList) {
        List<Course> courses = courseList.getUnit().getItems();
        setCategoryInfo(courses, category, subcategory, topic);
        scraperRepository.saveAll(courseList.getUnit().getItems());
    }

    private void setCategoryInfo(List<Course> courseList, Category category, Category subcategory, Category topic) {
        courseList.forEach(course -> {
            course.setCategory(category.getTitle());
            course.setSubcategory(subcategory.getTitle());
            course.setTopic(topic.getTitle());
        });

    }
}
