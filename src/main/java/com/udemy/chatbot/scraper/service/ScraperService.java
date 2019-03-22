package com.udemy.chatbot.scraper.service;

public interface ScraperService {
    boolean scrapeContent();

    boolean retryFailedScrapingRequests();
}
