package com.getset.j7cc.chapter7;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class MyPriorityTask implements Runnable, Comparable<MyPriorityTask> {
    private int priority;

    public MyPriorityTask() {
        this.priority = ThreadLocalRandom.current().nextInt(10);
    }

    @Override
    public int compareTo(MyPriorityTask o) {
        return Integer.compare(this.priority, o.priority);
    }

    @Override
    public void run() {
        System.out.println("Task with priority[" + priority + "] started at " + new Date());
        try {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println("Task with priority[" + priority + "] finished.");
    }
}
