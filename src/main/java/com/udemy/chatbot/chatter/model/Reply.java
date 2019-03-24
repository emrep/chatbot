package com.udemy.chatbot.chatter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.udemy.chatbot.scraper.model.Course;
import com.udemy.chatbot.scraper.model.EnumScrapingState;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Reply {
    private String message;
    private List<Course> courses;
    private EnumScrapingState scrapingState;

    public Reply(String message, List<Course> courses, EnumScrapingState scrapingState) {
        this.message = message;
        this.courses = new ArrayList<>();
        this.scrapingState = scrapingState;
        courses.forEach(courseCursor -> {
            Course course = new Course(courseCursor.getTitle(), courseCursor.getUrl(), courseCursor.getInstructors());
            this.courses.add(course);
        });
    }
}
