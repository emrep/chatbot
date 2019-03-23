package com.udemy.chatbot.scraper.apicaller;

import com.udemy.chatbot.scraper.model.CourseType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
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
        apiCallQueue.addQueue(() -> Arrays.asList(new CourseType(), new CourseType()));
        List<CourseType> categories = apiCaller.getCategories(apiCallQueue);
        assertEquals(5, categories.size());
        assertTrue(apiCallQueue.isQueueEmpty());
    }

    @Test
    @DisplayName("When getting an exception during an api request, add the api request to failed queue")
    void addFailedQueue() {
        ApiCallQueue<List<CourseType>> apiCallQueue = new ApiCallQueue<>();
        apiCallQueue.addQueue(() -> {throw new Exception();});
        List<CourseType> categories = apiCaller.getCategories(apiCallQueue);
        assertEquals(0, categories.size());
        assertFalse(apiCallQueue.isFailedQueueEmpty());
    }

    @Test
    void saveFirstCoursePage() {
    }

    @Test
    void saveOtherCoursePages() {
    }
}