package com.udemy.chatbot.rivechatbot.rest;

import com.udemy.chatbot.rivechatbot.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class BotResource {

    private final BotService botService;

    @Autowired
    public BotResource(BotService botService) {
        this.botService = botService;
    }

    @RequestMapping(value = "/chatbot/{message:.+}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity getUser(@PathVariable String message) throws IOException {
        return ResponseEntity.ok(botService.reply(message));
    }
}
