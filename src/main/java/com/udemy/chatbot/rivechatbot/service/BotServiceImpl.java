package com.udemy.chatbot.rivechatbot.service;

import com.rivescript.RiveScript;
import com.udemy.chatbot.rivechatbot.dao.CourseRepository;
import com.udemy.chatbot.scraper.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BotServiceImpl implements BotService {

    private static final String TOPIC = "topic";
    private final RiveScript bot;
    private final CourseRepository courseRepository;

    @Autowired
    public BotServiceImpl(RiveScript bot, CourseRepository courseRepository) {
        this.bot = bot;
        this.courseRepository = courseRepository;
    }

    @Override
    public String reply(String botUserName, String message) {
        StringBuilder reply = new StringBuilder(bot.reply(botUserName, message));
        String topic = bot.getUservar(botUserName, TOPIC);
        List<Course> suggestedCourses = courseRepository.findCourses(topic, 1);
        suggestedCourses.forEach(course -> reply.append("\n" + course));
        return reply.toString();
    }


}
