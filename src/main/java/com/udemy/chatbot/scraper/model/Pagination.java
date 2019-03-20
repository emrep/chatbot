package com.udemy.chatbot.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Pagination {
    @JsonProperty("total_page")
    private @Setter @Getter int totalPage;
    @JsonProperty("current_page")
    private @Setter @Getter int currentPage;
    private @Setter @Getter Next next;
}
