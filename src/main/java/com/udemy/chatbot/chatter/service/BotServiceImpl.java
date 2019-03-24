package com.udemy.chatbot.chatter.service;

import com.rivescript.RiveScript;
import com.udemy.chatbot.chatter.dao.CourseRepository;
import com.udemy.chatbot.chatter.model.Reply;
import com.udemy.chatbot.scraper.model.Course;
import com.udemy.chatbot.scraper.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BotServiceImpl implements BotService {

    private static final  String []COURSE_LIST_MESSAGES = {"You may want to take a look at the following courses:",
            "The following courses can be helpful!!", "Please take a look at the following courses:",
            "We hope the following courses are what you are exactly looking for:"};
    private static final String NOT_FOUND_COURSE_MESSAGE = "Sorry!! We could not find any course that is related to the keywords you entered";

    private static final String BOT_VAR_TOPIC = "topic";
    private static final String BOT_VAR_PRICE = "price";
    private static final String BOT_VAR_LEVEL = "level";
    private static final String BOT_VAR_SORT = "sort";

    private final RiveScript bot;
    private final CourseRepository courseRepository;
    private final ScraperService scraperService;


    @Autowired
    public BotServiceImpl(RiveScript bot, CourseRepository courseRepository, ScraperService scraperService) {
        this.bot = bot;
        this.courseRepository = courseRepository;
        this.scraperService = scraperService;
    }

    @Override
    public Reply reply(String botUserName, String message) {
        clearBotVariables(botUserName);
        String repliedMessage = bot.reply(botUserName, message);

        String topic = getVariableFromBot(botUserName, BOT_VAR_TOPIC);
        String price = getVariableFromBot(botUserName, BOT_VAR_PRICE);
        String level = getVariableFromBot(botUserName, BOT_VAR_LEVEL);
        String sort = getVariableFromBot(botUserName, BOT_VAR_SORT);

        List<Course> suggestedCourses = new ArrayList<>();

        if(!topic.isEmpty() || !price.isEmpty() || !level.isEmpty() || !sort.isEmpty()) {
            suggestedCourses = courseRepository.findCourses(topic, price, level, sort,  1);
            repliedMessage = setRepliedMessage(repliedMessage, suggestedCourses);
        }

        return new Reply(repliedMessage, suggestedCourses, scraperService.getScrapingState());
    }

    private String setRepliedMessage(String repliedMessage, List<Course> suggestedCourses) {
        if(suggestedCourses.isEmpty()) {
            repliedMessage = NOT_FOUND_COURSE_MESSAGE;
        } else {
            Random rnd = new Random();
            repliedMessage += " " + COURSE_LIST_MESSAGES[rnd.nextInt(COURSE_LIST_MESSAGES.length)];
        }
        return repliedMessage;
    }

    private String getVariableFromBot(String botUserName, String varName) {
        String botVar = bot.getUservar(botUserName, varName);
        return Objects.isNull(botVar) || botVar.equals("undefined") || botVar.equals("random") ? "" : botVar;
    }

    private void clearBotVariables(String botUserName) {
        bot.setUservar(botUserName, BOT_VAR_TOPIC, "");
        bot.setUservar(botUserName, BOT_VAR_PRICE, "");
        bot.setUservar(botUserName, BOT_VAR_LEVEL, "");
        bot.setUservar(botUserName, BOT_VAR_SORT, "");
    }


}
