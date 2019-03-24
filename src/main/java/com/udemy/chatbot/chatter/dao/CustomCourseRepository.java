package com.udemy.chatbot.chatter.dao;

import com.udemy.chatbot.scraper.model.Course;

import java.util.List;

public interface CustomCourseRepository {
    List<Course> findCourses(String topic, String price, String level, String sort, int page);
}
