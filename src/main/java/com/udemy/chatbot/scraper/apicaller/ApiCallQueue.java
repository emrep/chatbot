package com.udemy.chatbot.scraper.apicaller;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

@Component
@Scope("prototype")
@Data
public class ApiCallQueue {
    private Queue<ApiCallSupplier> queued = new LinkedList<>();
    private Queue<ApiCallSupplier> complete = new LinkedList<>();
    private Queue<ApiCallSupplier> failed = new LinkedList<>();


    public void addQueue(ApiCallSupplier apiCallSupplier) {
        queued.add(apiCallSupplier);
    }

    void addFailed(ApiCallSupplier apiCallSupplier) {
       failed.add(apiCallSupplier);
    }

    void addComplete(ApiCallSupplier apiCallSupplier) {
        complete.add(apiCallSupplier);
    }

}
