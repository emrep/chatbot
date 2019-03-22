package com.udemy.chatbot.scraper.apicaller;

@FunctionalInterface
public interface ApiCallSupplier<T> {
    T get() throws Exception;
}
