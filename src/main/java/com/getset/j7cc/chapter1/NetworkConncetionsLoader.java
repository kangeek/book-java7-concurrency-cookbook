package com.getset.j7cc.chapter1;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NetworkConncetionsLoader implements Runnable {
    @Override
    public void run() {
        System.out.println("Beginning network connections loading: " + new Date());
        try {
            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Finished network connections loading: " + new Date());
    }
}
