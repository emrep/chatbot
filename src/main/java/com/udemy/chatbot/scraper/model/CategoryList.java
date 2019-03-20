package com.udemy.chatbot.scraper.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class CategoryList {
    private List<Category> results;
    public CategoryList() {
        results = new ArrayList<>();
    }
}
