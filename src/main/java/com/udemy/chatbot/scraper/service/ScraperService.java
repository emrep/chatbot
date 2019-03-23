package com.udemy.chatbot.scraper.service;

import com.udemy.chatbot.scraper.model.EnumScrapingState;

public interface ScraperService {
    boolean scrapeContent();

    boolean retryFailedScrapingRequests();

    EnumScrapingState getScrapingState();
}
