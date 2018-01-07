package com.getset.j7cc.chapter7;

import java.util.concurrent.ThreadFactory;

public class MyThreadFactory implements ThreadFactory {
    private int count;
    private String prefix;

    public MyThreadFactory(String prefix) {
        this.prefix = prefix;
        this.count = 0;
    }

    @Override
    public Thread newThread(Runnable r) {
        System.out.println("Create a new thread with name: " + prefix + "-" + count);
        return new MyThread(r, prefix + "-" + count++);
    }
}
