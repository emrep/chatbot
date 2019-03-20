package com.udemy.chatbot.scraper.service;

import com.udemy.chatbot.scraper.dao.CourseRepository;
import com.udemy.chatbot.scraper.model.CategoryList;
import com.udemy.chatbot.scraper.model.CourseList;
import com.udemy.chatbot.scraper.model.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class CourseServiceImpl implements CourseService{
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
    private final CourseRepository courseRepository;

    @Autowired
    public CourseServiceImpl(RestTemplate restTemplate, CourseRepository courseRepository) {
        this.restTemplate = restTemplate;
        this.courseRepository = courseRepository;
    }

    @Override
    public void scrapeContent() {
        CategoryList categories  = restTemplate.getForObject(COURSE_CATEGORIES_URL, CategoryList.class);
        CategoryList subCategories  = restTemplate.getForObject(COURSE_CATEGORIES_URL + URL_DELIMETER + categories.getResults().get(0).getId() + SUBCATEGORIES, CategoryList.class);
        CategoryList topic = restTemplate.getForObject(SUBCATEGORIES_URL + URL_DELIMETER + subCategories.getResults().get(0).getId() + LABELS, CategoryList.class);
        CourseList courseList = saveFirstCoursePage(topic);
        saveOtherCoursePages(courseList.getUnit().getPagination());
    }

    private CourseList saveFirstCoursePage(CategoryList topic) {
        CourseList courseList = restTemplate.getForObject(COURSE_URL + topic.getResults().get(0).getId() + COURSE_FILTER + PAGE_SIZE, CourseList.class);
        courseRepository.saveAll(courseList.getUnit().getItems());
        return courseList;
    }

    private void saveOtherCoursePages(Pagination pagination) {
        while(Objects.nonNull(pagination.getNext())) {
            CourseList courseList = restTemplate.getForObject(UDEMY_URL + pagination.getNext().getUrl(), CourseList.class);
            pagination = courseList.getUnit().getPagination();
            courseRepository.saveAll(courseList.getUnit().getItems());
        }
    }
}
