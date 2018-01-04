package com.getset.j7cc.chapter4;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class CallableTask implements Callable<Integer> {
    private String name;

    public CallableTask(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Integer call() {
        int duration = 0;
        try {
            duration = new Random().nextInt(10);
//            System.out.println(name + " : CallableTask starts on " + new Date());
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
//            System.out.println(name + " will take " + duration + " seconds, but interrupted.");
        }

        System.out.println(name + " : CallableTask ended after " + duration + " seconds.");

        return duration;
    }

    @Override
    public String toString() {
        return "CallableTask{" +
                "name='" + name + '\'' +
                '}';
    }
}
