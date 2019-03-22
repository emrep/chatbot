package com.udemy.chatbot.scraper.apicaller;

import com.udemy.chatbot.scraper.model.CourseType;
import com.udemy.chatbot.scraper.model.Pagination;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ApiCallerImpl implements ApiCaller {

    @Override
    public List<CourseType> getCategories(ApiCallQueue apiCallQueue) {
        List<CourseType> resultList = new ArrayList<>();
        while (!apiCallQueue.getQueued().isEmpty()) {
            ApiCallSupplier apiCallSupplier = apiCallQueue.getQueued().poll();
            try {
                assert apiCallSupplier != null;
                List<CourseType> categoryList = (List<CourseType>) apiCallSupplier.get();
                resultList.addAll(categoryList);
                apiCallQueue.addComplete(apiCallSupplier);
            } catch (Exception e) {
                apiCallQueue.addFailed(apiCallSupplier);
            }
            break;
        }
        return resultList;
    }

    @Override
    public List<Pagination> saveFirstCoursePage(ApiCallQueue apiCallQueue) {
        List<Pagination> resultList = new ArrayList<>();
        while (!apiCallQueue.getQueued().isEmpty()) {
            ApiCallSupplier apiCallSupplier = apiCallQueue.getQueued().poll();
            try {
                assert apiCallSupplier != null;
                Pagination pagination = (Pagination) apiCallSupplier.get();
                resultList.add(pagination);
                apiCallQueue.addComplete(apiCallSupplier);
            } catch (Exception e) {
                apiCallQueue.addFailed(apiCallSupplier);
            }
            break;
        }
        return resultList;
    }

    @Override
    public void saveOtherCoursePages(ApiCallQueue apiCallQueue) {
        while (!apiCallQueue.getQueued().isEmpty()) {
            ApiCallSupplier apiCallSupplier = apiCallQueue.getQueued().poll();
            try {
                assert apiCallSupplier != null;
                apiCallSupplier.get();
                apiCallQueue.addComplete(apiCallSupplier);
            } catch (Exception e) {
                apiCallQueue.addFailed(apiCallSupplier);
            }
            return;
        }
    }
}
