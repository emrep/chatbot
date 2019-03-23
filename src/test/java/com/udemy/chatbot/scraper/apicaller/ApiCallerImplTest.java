package com.udemy.chatbot.scraper.apicaller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ApiCallerImplTest {

    @Mock
    ApiCaller apiCaller;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("get categoris from the API")
    void getCategories() {
        assertNotNull(apiCaller);
    }

    @Test
    void saveFirstCoursePage() {
    }

    @Test
    void saveOtherCoursePages() {
    }
}