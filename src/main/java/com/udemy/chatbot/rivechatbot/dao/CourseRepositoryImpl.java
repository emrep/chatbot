package com.udemy.chatbot.rivechatbot.dao;

import com.udemy.chatbot.scraper.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

@Repository
public class CourseRepositoryImpl implements CustomCourseRepository {

    private final MongoOperations operations;

    private static final int NUMBER_OF_RECORDS = 3;

    @Autowired
    public CourseRepositoryImpl(MongoOperations operations) {
        Assert.notNull(operations, "MongoOperations must not be null!");
        this.operations = operations;
    }

    @Override
    public List<Course> findCourses(String topic, int page) {
        final Query query = new Query();
        Criteria topicCriteria = Criteria.where("topic").regex("(?i)\\b" + topic + "\\b");
        Criteria titleCriteria = Criteria.where("title").regex("(?i)\\b" + topic + "\\b");
        Criteria headlineCriteria = Criteria.where("headline").regex("(?i)\\b" + topic + "\\b");
        query.addCriteria(new Criteria().orOperator(topicCriteria,titleCriteria, headlineCriteria));
        query.with(new Sort(Sort.Direction.DESC, "avgRatingRecent"));

        final Pageable pageableRequest = PageRequest.of(page, NUMBER_OF_RECORDS);
        query.with(pageableRequest);

        return this.operations.find(query, Course.class);
    }
}