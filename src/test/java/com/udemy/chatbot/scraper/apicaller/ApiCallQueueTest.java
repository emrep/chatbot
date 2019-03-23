package com.udemy.chatbot.scraper.apicaller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiCallQueueTest {

    @Test
    @DisplayName("move items from failed queue to the Queue")
    void moveFailedToQueued() {
        ApiCallQueue<Integer> apiCallQueue = new ApiCallQueue();
        apiCallQueue.addFailed(() -> 1);
        apiCallQueue.addFailed(() -> 2);
        apiCallQueue.addFailed(() -> 3);

        assertFalse(apiCallQueue.isFailedQueueEmpty());
        apiCallQueue.moveFailedToQueued();
        assertTrue(apiCallQueue.isFailedQueueEmpty());
        assertFalse(apiCallQueue.isQueueEmpty());
    }
}