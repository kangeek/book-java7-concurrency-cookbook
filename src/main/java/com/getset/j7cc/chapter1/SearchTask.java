package com.getset.j7cc.chapter1;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SearchTask implements Runnable {
    private ThreadGroupResult result;

    public SearchTask(ThreadGroupResult result) {
        this.result = result;
    }

    @Override
    public void run() {
        String name=Thread.currentThread().getName();
        System.out.printf("Thread %s: Start\n",name);
        try {
            doTask();
            result.setName(name);
        } catch (InterruptedException e) {
            System.out.printf("Thread %s: Interrupted\n",name);
            return;
        }
        System.out.printf("Thread %s: End\n",name);
    }

    private void doTask() throws InterruptedException {
        Random random = new Random((new Date()).getTime());
        int value=(int)(random.nextDouble() * 100);
        System.out.printf("Thread %s: %d\n",Thread.currentThread().getName(), value);
        TimeUnit.SECONDS.sleep(value);
    }
}
