package com.getset.j7cc.chapter6;

import java.util.Deque;

public class AddTask implements Runnable {
    private Deque<String> deque;

    public AddTask(Deque<String> deque) {
        this.deque = deque;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        for (int i = 0; i < 100; i++) {
            deque.add(name + "-" + i);
        }
    }
}
