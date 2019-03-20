package com.udemy.chatbot.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Pagination {
    @JsonProperty("total_page")
    private int totalPage;
    @JsonProperty("current_page")
    private int currentPage;
    private Next next;
}
