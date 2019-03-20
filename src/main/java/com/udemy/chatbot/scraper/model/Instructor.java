package com.udemy.chatbot.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Instructor {
    private Long id;
    @JsonProperty("display_name")
    private String name;
}
