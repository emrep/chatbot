package com.udemy.chatbot.chatter.service;

import com.udemy.chatbot.chatter.model.Reply;

public interface BotService {
    Reply reply(String botUserName, String message);
}
