package com.udemy.chatbot.rivechatbot.dao;

import com.udemy.chatbot.scraper.model.Course;

import java.util.List;

public interface CustomCourseRepository {
    List<Course> findCourses(String topic, int page);
}
