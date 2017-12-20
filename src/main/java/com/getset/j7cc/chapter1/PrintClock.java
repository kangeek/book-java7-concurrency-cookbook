package com.getset.j7cc.chapter1;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PrintClock implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println(new Date());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("Interrupted.");
            }
        }
    }
}
