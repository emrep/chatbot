package com.udemy.chatbot.scraper.dao;

import com.udemy.chatbot.scraper.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScraperRepository extends MongoRepository<Course, Long> {
}
