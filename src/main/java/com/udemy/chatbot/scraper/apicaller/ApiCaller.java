package com.udemy.chatbot.scraper.apicaller;

import java.util.List;

public interface ApiCaller {
    <T> List<T> run(ApiCallQueue<T> apiCallQueue, boolean waitOtherThreads);
}
