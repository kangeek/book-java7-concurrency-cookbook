package com.getset.j7cc.chapter6;

import java.util.Deque;

public class PollTask implements Runnable {
    private Deque<String> deque;

    public PollTask(Deque<String> deque) {
        this.deque = deque;
    }

    @Override
    public void run() {
        for (int i = 0; i < 50; i++) {
            deque.pollFirst();
            deque.pollLast();
        }
    }
}
