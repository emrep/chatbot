package com.udemy.chatbot.scraper.apicaller;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

@Component
@Scope("prototype")
public class ApiCallQueue<T> {
    private Queue<ApiCallSupplier<T>> queued = new LinkedList<>();
    private Queue<ApiCallSupplier<T>> complete = new LinkedList<>();
    private Queue<ApiCallSupplier<T>> failed = new LinkedList<>();

    public void addQueue(ApiCallSupplier<T> apiCallSupplier) {
        queued.add(apiCallSupplier);
    }

    void addFailed(ApiCallSupplier<T> apiCallSupplier) {
       failed.add(apiCallSupplier);
    }

    void addComplete(ApiCallSupplier<T> apiCallSupplier) {
        complete.add(apiCallSupplier);
    }

    ApiCallSupplier<T> getNext() {
        return queued.poll();
    }

    boolean isQueueEmpty() {
        return queued.isEmpty();
    }

    public boolean isFailedQueueEmpty() {
        return failed.isEmpty();
    }

}
