package com.udemy.chatbot.scraper.dao;

import com.udemy.chatbot.scraper.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

@Repository
public class ScraperRepositoryImpl implements CustomScraperRepository {

    private final MongoOperations operations;

    @Autowired
    public ScraperRepositoryImpl(MongoOperations operations) {
        this.operations = operations;
    }

    @Override
    public void dropCollection() {
        operations.dropCollection(Course.class);
    }
}
