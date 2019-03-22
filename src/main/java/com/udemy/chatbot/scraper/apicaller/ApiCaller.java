package com.udemy.chatbot.scraper.apicaller;

import com.udemy.chatbot.scraper.model.CourseType;
import com.udemy.chatbot.scraper.model.Pagination;

import java.util.List;

public interface ApiCaller {
    List<CourseType> getCategories(ApiCallQueue<List<CourseType>> apiCallQueue);

    List<Pagination> saveFirstCoursePage(ApiCallQueue<Pagination> apiCallQueue);

    void saveOtherCoursePages(ApiCallQueue<Boolean> apiCallQueue);
}
