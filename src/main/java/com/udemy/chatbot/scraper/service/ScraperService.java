package com.udemy.chatbot.scraper.service;

import com.udemy.chatbot.scraper.model.EnumScrapingState;

public interface ScraperService {
    void bootContentScraper();

    EnumScrapingState getScrapingState();

    void setScrapingState(EnumScrapingState scrapingState);
}
