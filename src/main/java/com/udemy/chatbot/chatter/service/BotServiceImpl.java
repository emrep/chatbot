package com.udemy.chatbot.chatter.service;

import com.rivescript.RiveScript;
import com.udemy.chatbot.chatter.dao.CourseRepository;
import com.udemy.chatbot.chatter.model.Reply;
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
    public Reply reply(String botUserName, String message) {
        String repliedMessage = bot.reply(botUserName, message);
        String topic = bot.getUservar(botUserName, TOPIC);
        List<Course> suggestedCourses = courseRepository.findCourses(topic, 1);
        return new Reply(repliedMessage, suggestedCourses);
    }


}
