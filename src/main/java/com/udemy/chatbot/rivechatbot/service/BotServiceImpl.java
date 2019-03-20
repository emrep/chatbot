package com.udemy.chatbot.rivechatbot.service;

import com.rivescript.RiveScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BotServiceImpl implements BotService {

    private final RiveScript bot;

    @Autowired
    public BotServiceImpl(RiveScript bot) {
        this.bot = bot;
    }

    @Override
    public String reply(String message) throws IOException {
       return bot.reply("user", message);
    }


}
