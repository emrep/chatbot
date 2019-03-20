package com.udemy.chatbot.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseUnit {
    private @Setter @Getter List<Course> items;
    private @Setter @Getter Pagination pagination;
    public CourseUnit() {
        this.items = new ArrayList<>();
    }
}
