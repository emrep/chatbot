package com.udemy.chatbot.scraper.service;

import com.udemy.chatbot.scraper.dao.CourseRepository;
import com.udemy.chatbot.scraper.model.CategoryList;
import com.udemy.chatbot.scraper.model.CourseList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CourseServiceImpl implements CourseService{
    private static final String UDEMY_API_URL = "https://www.udemy.com/api-2.0/";
    private static final String COURSE_CATEGORIES_URL = UDEMY_API_URL + "course-categories/";
    private static final String SUBCATEGORIES_URL = UDEMY_API_URL + "course-subcategories/";
    private static final String COURSE_URL = "https://www.udemy.com/api-2.0/discovery-units/all_courses/?source_page=topic_page&label_id=";

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
        CategoryList subCategories  = restTemplate.getForObject(COURSE_CATEGORIES_URL + categories.getResults().get(0).getId() + "/subcategories", CategoryList.class);
        CategoryList topic = restTemplate.getForObject(SUBCATEGORIES_URL + subCategories.getResults().get(0).getId() + "/labels", CategoryList.class);
        CourseList courseList = restTemplate.getForObject(COURSE_URL + topic.getResults().get(0).getId() + "&fl=lbl&sos=pl", CourseList.class);
        courseRepository.saveAll(courseList.getUnit().getItems());
    }
}
