package com.udemy.chatbot.scraper.rest;

import com.udemy.chatbot.scraper.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScraperResource {

    private final CourseService courseService;

    @Autowired
    public ScraperResource(CourseService courseService) {
        this.courseService = courseService;
    }

    @RequestMapping(value = "/scraping", method = RequestMethod.GET)
    public ResponseEntity scraping() {
        courseService.scrapeContent();
        return ResponseEntity.ok("ok");
    }

}
