package com.udemy.chatbot.rivechatbot.rest;

import com.udemy.chatbot.rivechatbot.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Random;

@RestController
public class BotResource {

    public static final String BOT_USER_NAME = "botUserName";
    private final BotService botService;

    @Autowired
    public BotResource(BotService botService) {
        this.botService = botService;
    }

    @RequestMapping(value = "/chatbot/{message:.+}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity reply(@PathVariable String message, HttpServletResponse response, @CookieValue(value = BOT_USER_NAME, defaultValue = "") String botUserName) {
        botUserName = botUserName.isEmpty() ? addBotUserNameCookie(response) : botUserName;
        return ResponseEntity.ok(botService.reply(botUserName, message));
    }

    private String addBotUserNameCookie(HttpServletResponse response) {
        Random rnd = new Random();
        String botUserName = String.valueOf(rnd.nextInt(Integer.MAX_VALUE));
        Cookie cookie= new Cookie(BOT_USER_NAME, botUserName);
        response.addCookie(cookie);
        return botUserName;
    }
}
