package com.getset.j7cc.chapter6;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.DelayQueue;

public class DelayedEventTask implements Runnable {
    private int threadId;
    private DelayQueue<DelayedEvent> queue;

    public DelayedEventTask(int threadId, DelayQueue<DelayedEvent> queue) {
        this.threadId = threadId;
        this.queue = queue;
    }

    @Override
    public void run() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            queue.put(new DelayedEvent(new Date(System.currentTimeMillis() + (i * 100) + random.nextInt(10))));
        }
    }
}
