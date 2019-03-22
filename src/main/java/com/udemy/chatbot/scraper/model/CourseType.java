package com.udemy.chatbot.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CourseType {
    private long id;
    private String title;
    private String category;
    private String subCategory;
    private String topic;
}
