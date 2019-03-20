package com.udemy.chatbot.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Course {
    private Long id;
    private Category category;
    private Category subcategory;
    private Category topic;
    private String title;
    private String headline;
    @JsonProperty("num_subscribers")
    private int subscriberNumber;
    @JsonProperty("avg_rating")
    private Double avgRating;
    @JsonProperty("avg_rating_recent")
    private Double avgRatingRecent;
    private Double rating;
    private String url;
    @JsonProperty("is_paid")
    private boolean paid;
    @JsonProperty("visible_instructors")
    private List<Instructor> instructors;
}
