package com.getset.j7cc.chapter6;

import java.util.concurrent.PriorityBlockingQueue;

public class EventTask implements Runnable {
    private int threadId;
    private PriorityBlockingQueue<Event> queue;

    public EventTask(int threadId, PriorityBlockingQueue<Event> queue) {
        this.threadId = threadId;
        this.queue = queue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            queue.add(new Event(threadId, i));
        }
    }
}
