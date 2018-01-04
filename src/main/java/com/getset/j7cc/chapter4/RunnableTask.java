package com.getset.j7cc.chapter4;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RunnableTask implements Runnable {
    private Date initDate;
    private String name;

    public RunnableTask(String name) {
        this.initDate = new Date();
        this.name = name;
    }

    @Override
    public void run() {
//        System.out.println(name + " : RunnableTask created on: " + initDate);
        System.out.println(name + " : RunnableTask started on: " + new Date());

        int duration = 0;
        try {
            duration = new Random().nextInt(10);
//            System.out.println(name + " : RunnableTask running for " + duration + " seconds...");
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(name + " : RunnableTask ended after " + duration + " seconds.");
    }

    @Override
    public String toString() {
        return "RunnableTask{" +
                "initDate=" + initDate +
                ", name='" + name + '\'' +
                '}';
    }
}
