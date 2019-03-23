package com.udemy.chatbot;

import com.udemy.chatbot.scraper.service.ScraperService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ChatbotApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ChatbotApplication.class, args);
		ScraperService scraperService = context.getBean(ScraperService.class);
		boolean isSuccessful = scraperService.scrapeContent();
		while(!isSuccessful) {// TODO: 22-Mar-19 Scheduling a 'Retry Scraping Task'
			isSuccessful = scraperService.retryFailedScrapingRequests();
		}
	}

}
