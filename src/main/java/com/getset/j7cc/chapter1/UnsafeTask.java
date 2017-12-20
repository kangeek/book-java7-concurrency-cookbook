package com.getset.j7cc.chapter1;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class UnsafeTask implements Runnable {
    private Integer i;

    @Override
    public void run() {
        i = new Random().nextInt(100);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " - number: " + i);
    }
}
