package com.udemy.chatbot.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class Course {
    private Long id;
    private String category;
    private String subcategory;
    private String topic;
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
    @JsonProperty("price_detail")
    private Price price;
    @JsonProperty("visible_instructors")
    private List<Instructor> instructors;
    @JsonProperty("published_time")
    private LocalDate publishedTime;
    @JsonProperty("instructional_level_simple")
    private String instructionalLevel;

    public Course() {
    }

    public Course(String title, String url, List<Instructor> instructors) {
        this.title = title;
        this.url = url;
        this.instructors = instructors;
    }
}
