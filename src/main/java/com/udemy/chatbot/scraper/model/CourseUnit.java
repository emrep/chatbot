package com.udemy.chatbot.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CourseUnit {
    private List<Course> items;
    private Pagination pagination;
    public CourseUnit() {
        this.items = new ArrayList<>();
    }
}
