package com.udemy.chatbot.rivechatbot.service;

import java.io.IOException;

public interface BotService {
    String reply(String message) throws IOException;
}
