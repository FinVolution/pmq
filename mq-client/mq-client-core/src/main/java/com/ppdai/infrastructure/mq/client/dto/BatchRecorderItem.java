package com.ppdai.infrastructure.mq.client.dto;

import java.util.concurrent.atomic.AtomicInteger;

public class BatchRecorderItem {
    private volatile long batchReacorderId;
    private volatile int threadCount;
    private AtomicInteger counter = new AtomicInteger(0);
    // public int counter = 0;
    private volatile long maxId = 0;
    private volatile boolean batchFinished = false;

    public long getBatchReacorderId() {
        return batchReacorderId;
    }

    public void setBatchReacorderId(long batchReacorderId) {
        this.batchReacorderId = batchReacorderId;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public AtomicInteger getCounter() {
        return counter;
    }

    public void setCounter(AtomicInteger counter) {
        this.counter = counter;
    }

    public long getMaxId() {
        return maxId;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }

    public boolean isBatchFinished() {
        return batchFinished;
    }

    public void setBatchFinished(boolean batchFinished) {
        this.batchFinished = batchFinished;
    }
}