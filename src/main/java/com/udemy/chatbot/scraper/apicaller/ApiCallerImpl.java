package com.udemy.chatbot.scraper.apicaller;

import com.udemy.chatbot.scraper.model.CourseType;
import com.udemy.chatbot.scraper.model.Pagination;
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

    @Value("${api.caller.thread.number}")
    private int threadNumber;

    @Value("${limited.data}")
    private boolean limitedData;

    @Override
    public List<CourseType> getCategories(ApiCallQueue<List<CourseType>> apiCallQueue) {
        List<CourseType> resultList = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        while (!apiCallQueue.isQueueEmpty()) {
            ApiCallSupplier<List<CourseType>> apiCallSupplier = apiCallQueue.getNext();
            try {
                Future<List<CourseType>> future = executorService.submit(apiCallSupplier::get);
                resultList.addAll(future.get());
                apiCallQueue.addComplete(apiCallSupplier);
            } catch (Exception e) {
                apiCallQueue.addFailed(apiCallSupplier);
            }
        }
        awaitTerminationAfterShutdown(executorService);
        return resultList;
    }

    @Override
    public List<Pagination> saveFirstCoursePage(ApiCallQueue<Pagination> apiCallQueue) {
        List<Pagination> resultList = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        while (!apiCallQueue.isQueueEmpty()) {
            ApiCallSupplier<Pagination> apiCallSupplier = apiCallQueue.getNext();
            try {
                Future<Pagination> future = executorService.submit(apiCallSupplier::get);
                resultList.add(future.get());
                apiCallQueue.addComplete(apiCallSupplier);
            } catch (Exception e) {
                apiCallQueue.addFailed(apiCallSupplier);
            }
        }
        awaitTerminationAfterShutdown(executorService);
        return resultList;
    }

    @Override
    public void saveOtherCoursePages(ApiCallQueue<Boolean> apiCallQueue) {
        if(limitedData) {
            return;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        while (!apiCallQueue.isQueueEmpty()) {
            ApiCallSupplier<Boolean> apiCallSupplier = apiCallQueue.getNext();
            try {
                executorService.submit(apiCallSupplier::get);
                apiCallQueue.addComplete(apiCallSupplier);
            } catch (Exception e) {
                apiCallQueue.addFailed(apiCallSupplier);
            }
        }
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
