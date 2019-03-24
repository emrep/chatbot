package com.udemy.chatbot.scraper.apicaller;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
public class ApiCallerImpl implements ApiCaller {

    @Value("${api.request.thread.number}")
    private @Setter int threadNumber;

    @Override
    public <T> List<T> run(ApiCallQueue<T> apiCallQueue, boolean waitOtherThreads) {
        List<T> resultList = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        while (!apiCallQueue.isQueueEmpty()) {
            ApiCallSupplier<T> apiCallSupplier = apiCallQueue.getNext();
            try {
                Future<T> future = executorService.submit(apiCallSupplier::get);
                resultList.add(future.get());
                apiCallQueue.addComplete(apiCallSupplier);
            } catch (Exception e) {
                apiCallQueue.addFailed(apiCallSupplier);
            }
        }
        if(waitOtherThreads) {
            awaitTerminationAfterShutdown(executorService);
        }
        return resultList;
    }

    private void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
