package com.udemy.chatbot.scraper.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class CourseTypeList {
    private List<CourseType> results;
    public CourseTypeList() {
        results = new ArrayList<>();
    }
}
