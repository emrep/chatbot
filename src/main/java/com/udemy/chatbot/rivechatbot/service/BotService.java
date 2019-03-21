package com.udemy.chatbot.rivechatbot.service;

import com.udemy.chatbot.rivechatbot.model.Reply;

public interface BotService {
    Reply reply(String botUserName, String message);
}
