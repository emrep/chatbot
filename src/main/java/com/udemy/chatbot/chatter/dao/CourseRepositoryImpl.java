package com.udemy.chatbot.chatter.dao;

import com.udemy.chatbot.scraper.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class CourseRepositoryImpl implements CustomCourseRepository {

    private final MongoOperations operations;

    @Value("${chatbot.suggested.course.number}")
    private int suggestedCourseNumber = 3;

    @Autowired
    public CourseRepositoryImpl(MongoOperations operations) {
        Assert.notNull(operations, "MongoOperations must not be null!");
        this.operations = operations;
    }

    @Override
    public List<Course> findCourses(String topic, String price, String level, String sort, int page) {

        final Query query = new Query();

        if(!topic.isEmpty()) {
            Criteria topicCriteria = Criteria.where("topic").regex("(?i)\\b" + topic + "\\b");
            Criteria titleCriteria = Criteria.where("title").regex("(?i)\\b" + topic + "\\b");
            Criteria headlineCriteria = Criteria.where("headline").regex("(?i)\\b" + topic + "\\b");
            query.addCriteria(new Criteria().orOperator(topicCriteria,titleCriteria, headlineCriteria));
        }

        if(!price.isEmpty()) {
            boolean isPaid = price.equals("paid");
            query.addCriteria(Criteria.where("paid").is(isPaid));
        }

        if(!level.isEmpty()) {
            query.addCriteria(Criteria.where("instructionalLevel").regex("(?i)\\b" + level + "\\b"));
        }

        if(!sort.isEmpty()) {
            if(sort.equals("newest")) {
                query.with(new Sort(Sort.Direction.DESC, "publishedTime"));
            }  else if(sort.equals("cheapest")) {
                query.with(new Sort(Sort.Direction.ASC, "price.amount"));
            } else if(sort.equals("expensive")) {
                query.with(new Sort(Sort.Direction.DESC, "price.amount"));
            } else if(sort.equals("bestseller")) {
                query.with(new Sort(Sort.Direction.DESC, "subscriberNumber"));
            } else if(sort.equals("popular")) {
                query.with(new Sort(Sort.Direction.DESC, "subscriberNumber"));
            }
        } else {
            query.with(new Sort(Sort.Direction.DESC, "avgRatingRecent"));
        }

        final Pageable pageableRequest = PageRequest.of(page, suggestedCourseNumber);
        query.with(pageableRequest);

        return this.operations.find(query, Course.class);
    }
}
