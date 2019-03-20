package com.udemy.chatbot.scraper.rest;

import com.udemy.chatbot.scraper.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScraperResource {

    private final ScraperService scraperService;

    @Autowired
    public ScraperResource(ScraperService scraperService) {
        this.scraperService = scraperService;
    }

    @RequestMapping(value = "/scraping", method = RequestMethod.GET)
    public ResponseEntity scraping() {
        scraperService.scrapeContent();
        return ResponseEntity.ok("ok");
    }

}
