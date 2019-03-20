package com.udemy.chatbot.scraper.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class CategoryList {

    private @Getter @Setter List<Category> results;

    public CategoryList() {
        results = new ArrayList<>();
    }

}
