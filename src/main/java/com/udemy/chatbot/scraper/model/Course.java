package com.udemy.chatbot.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Course {
    private @Setter @Getter Long id;
    private @Setter @Getter String title;
    private @Setter @Getter String url;
    @JsonProperty("is_paid")
    private @Setter @Getter boolean paid;

}
