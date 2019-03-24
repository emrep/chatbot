package com.udemy.chatbot.scraper.apicaller;

import com.udemy.chatbot.scraper.model.CourseType;
import com.udemy.chatbot.scraper.model.Pagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApiCallerImplTest {

    ApiCallerImpl apiCaller;

    @BeforeEach
    void setUp() {
        apiCaller = new ApiCallerImpl();
        apiCaller.setThreadNumber(1);
    }

    @Test
    @DisplayName("poll api requests from the Queue")
    void getCategoriesFromQueue() {
        ApiCallQueue<List<CourseType>> apiCallQueue = new ApiCallQueue<>();
        apiCallQueue.addQueue(() -> Arrays.asList(new CourseType(), new CourseType(), new CourseType()));
        apiCallQueue.addQueue(() -> Arrays.asList(new CourseType(), new CourseType(), new CourseType(), new CourseType()));
        List<List<CourseType>> categories = apiCaller.run(apiCallQueue, true);
        assertEquals(2, categories.size());
        assertTrue(apiCallQueue.isQueueEmpty());
    }

    @Test
    @DisplayName("move the api request to the Failed Queue When getting an exception from an api request")
    void addFailedQueue() {
        ApiCallQueue<Pagination> apiCallQueue = new ApiCallQueue<>();
        apiCallQueue.addQueue(() -> {throw new Exception();});
        List<Pagination> pages = apiCaller.run(apiCallQueue, false);
        assertEquals(0, pages.size());
        assertFalse(apiCallQueue.isFailedQueueEmpty());
    }
}