package com.udemy.chatbot.scraper.apicaller;

import com.udemy.chatbot.scraper.model.CourseType;
import com.udemy.chatbot.scraper.model.Pagination;

import java.util.List;

public interface ApiCaller {
    <T> List<T> run(ApiCallQueue<T> apiCallQueue, boolean waitOtherThreads);
}
