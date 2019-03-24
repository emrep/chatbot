package com.udemy.chatbot.chatter.service;

import com.rivescript.RiveScript;
import com.udemy.chatbot.chatter.dao.CourseRepository;
import com.udemy.chatbot.chatter.model.Reply;
import com.udemy.chatbot.scraper.model.Course;
import com.udemy.chatbot.scraper.service.ScraperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BotServiceImplTest {

    private BotService botService;

    @Mock
    private RiveScript bot;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private ScraperService scraperService;

    @BeforeEach
    void setUp() {
        botService = new BotServiceImpl(bot, courseRepository, scraperService);
    }

    @Test
    @DisplayName("get replied message from chatbot library")
    void reply() {
        when(bot.reply("user", "Hello")).thenReturn("Hi");
        Reply reply = botService.reply("user", "Hello");
        assertEquals(reply.getMessage(), "Hi");
    }

    @Test
    @DisplayName("get not found course message")
    void getNotFoundMessage() {
        when(bot.reply("user", "Hello")).thenReturn("Hi");
        when(bot.getUservar("user", BotServiceImpl.BOT_VAR_TOPIC)).thenReturn("Not Found Course");
        Reply reply = botService.reply("user", "Hello");
        assertEquals(reply.getMessage(), BotServiceImpl.NOT_FOUND_COURSE_MESSAGE);
    }

    @Test
    @DisplayName("get message with the course list")
    void getMessageWithCourseList() {
        when(bot.reply("user", "Hello")).thenReturn("Hi");
        when(bot.getUservar("user", BotServiceImpl.BOT_VAR_TOPIC)).thenReturn("Java");
        List<Course> suggestedCourses = Arrays.asList(new Course(), new Course(), new Course());
        when(courseRepository.findCourses("Java", "", "", "", 1)).thenReturn(suggestedCourses);
        Reply reply = botService.reply("user", "Hello");
        assertEquals(reply.getCourses().size(), 3);
    }
}