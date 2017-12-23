package com.getset.j7cc.chapter2;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class EventStorage {
    private int maxSize;
    private LinkedList<Date> storage;

    public EventStorage() {
        this.maxSize = 20;
        this.storage = new LinkedList<>();
    }

    public synchronized void set() {
        while (storage.size() == maxSize) {
            // 如果满了，调用wait()方 法，直到storage有空的空间
            try {
                wait();
                System.out.println("Set Wait...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        storage.add(new Date());
        System.out.println("Set: " + storage.size());
        // 调用notifyAll()方法来唤醒所有在wait()方法上睡眠的线程
        notifyAll();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void get() {
        while (storage.size() == 0) {
            // 如果没有，调用wait()方法直到，storage有一些事件
            try {
                wait();
                System.out.println("Get Wait...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Get: " + storage.size() + storage.poll());
        notifyAll();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
