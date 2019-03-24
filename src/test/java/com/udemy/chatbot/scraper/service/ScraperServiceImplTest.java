package com.udemy.chatbot.scraper.service;

import com.udemy.chatbot.scraper.apicaller.ApiCallQueue;
import com.udemy.chatbot.scraper.apicaller.ApiCallerImpl;
import com.udemy.chatbot.scraper.dao.ScraperRepository;
import com.udemy.chatbot.scraper.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScraperServiceImplTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ScraperRepository scraperRepository;
    @Mock
    private CourseTypeList courseTypeList;

    private ApiCallQueue<List<CourseType>> categoryQueue;
    private ApiCallQueue<List<CourseType>> subcategoryQueue;
    private ApiCallQueue<Pagination> topicQueue;
    private ApiCallQueue<Boolean> coursePageQueue;
    private ApiCallerImpl apiCaller;

    private ScraperServiceImpl scraperService;

    @BeforeEach
    void setUp() {
        categoryQueue = new ApiCallQueue<>();
        subcategoryQueue = new ApiCallQueue<>();
        topicQueue = new ApiCallQueue<>();
        coursePageQueue = new ApiCallQueue<>();
        apiCaller = new ApiCallerImpl();
        scraperService = new ScraperServiceImpl(restTemplate, scraperRepository, categoryQueue, subcategoryQueue, topicQueue, coursePageQueue, apiCaller);
    }

    @Test
    @DisplayName("scrape the content with one thread")
    void scrapeContent() {
        apiCaller.setThreadNumber(1);

        courseTypeList.getResults().addAll(Arrays.asList(new CourseType(), new CourseType(), new CourseType()));
        List<CourseType> courseTypes = Arrays.asList(new CourseType(), new CourseType(), new CourseType(), new CourseType(), new CourseType(), new CourseType());
        when(restTemplate.getForObject(anyString(), eq(CourseTypeList.class))).thenReturn(courseTypeList);
        when(courseTypeList.getResults()).thenReturn(courseTypes);

        CourseList courseList = new CourseList();
        courseList.setUnit(new CourseUnit());
        courseList.getUnit().setItems(Arrays.asList(new Course(), new Course(), new Course(), new Course()));
        Pagination pagination = new Pagination();
        courseList.getUnit().setPagination(pagination);
        doReturn(courseList).when(restTemplate).getForObject(anyString(), eq(CourseList.class));
        scraperService.bootContentScraper();
        assertEquals(EnumScrapingState.COMPLETED, scraperService.getScrapingState());
    }

    @Test
    @DisplayName("scrape the content with multiple thread")
    void scrapeContentWithMultipleThread() {
        apiCaller.setThreadNumber(4);

        courseTypeList.getResults().addAll(Arrays.asList(new CourseType(), new CourseType(), new CourseType()));
        List<CourseType> courseTypes = Arrays.asList(new CourseType(), new CourseType(), new CourseType(), new CourseType(), new CourseType(), new CourseType());
        when(restTemplate.getForObject(anyString(), eq(CourseTypeList.class))).thenReturn(courseTypeList);
        when(courseTypeList.getResults()).thenReturn(courseTypes);

        CourseList courseList = new CourseList();
        courseList.setUnit(new CourseUnit());
        courseList.getUnit().setItems(Arrays.asList(new Course(), new Course(), new Course(), new Course()));
        Pagination pagination = new Pagination();
        courseList.getUnit().setPagination(pagination);
        doReturn(courseList).when(restTemplate).getForObject(anyString(), eq(CourseList.class));

        scraperService.bootContentScraper();
        assertEquals(EnumScrapingState.COMPLETED, scraperService.getScrapingState());
    }


    @Test
    @DisplayName("rescrape the failed content")
    void retryFailedScrapingRequests() {
        apiCaller.setThreadNumber(3);
        courseTypeList.getResults().addAll(Arrays.asList(new CourseType(), new CourseType(), new CourseType()));
        List<CourseType> courseTypes = Arrays.asList(new CourseType(), new CourseType(), new CourseType(), new CourseType(), new CourseType(), new CourseType());
        when(restTemplate.getForObject(anyString(), eq(CourseTypeList.class))).thenReturn(courseTypeList);
        when(courseTypeList.getResults()).thenReturn(courseTypes);

        CourseList courseList = new CourseList();
        courseList.setUnit(new CourseUnit());
        courseList.getUnit().setItems(Arrays.asList(new Course(), new Course(), new Course(), new Course()));
        Pagination pagination = new Pagination();
        courseList.getUnit().setPagination(pagination);
        doReturn(courseList).when(restTemplate).getForObject(anyString(), eq(CourseList.class));

        categoryQueue.addFailed(() -> courseTypes);
        topicQueue.addFailed(Pagination::new);
        coursePageQueue.addFailed(() -> true);
        coursePageQueue.addFailed(() -> true);
        scraperService.setScrapingState(EnumScrapingState.PARTIALLY_COMPLETED);

        scraperService.bootContentScraper();
        assertEquals(EnumScrapingState.COMPLETED, scraperService.getScrapingState());
    }
}