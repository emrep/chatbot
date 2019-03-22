package com.udemy.chatbot.chatter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.udemy.chatbot.scraper.model.Course;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Reply implements Serializable {
    private static final long serialVersionUID = -8017228644483881284L;

    private String message;
    private List<Course> courses;

    public Reply(String message, List<Course> courses) {
        this.message = message;
        this.courses = new ArrayList<>();
        courses.forEach(courseCursor -> {
            Course course = new Course(courseCursor.getTitle(), courseCursor.getUrl(), courseCursor.getInstructors());
            this.courses.add(course);
        });
    }
}
