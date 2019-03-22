package com.udemy.chatbot.scraper.apicaller;

import com.udemy.chatbot.scraper.model.CourseType;
import com.udemy.chatbot.scraper.model.Pagination;

import java.util.List;

public interface ApiCaller {
    List<CourseType> getCategories(ApiCallQueue apiCallQueue);

    List<Pagination> saveFirstCoursePage(ApiCallQueue apiCallQueue);

    void saveOtherCoursePages(ApiCallQueue apiCallQueue);
}
