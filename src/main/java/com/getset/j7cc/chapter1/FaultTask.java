package com.getset.j7cc.chapter1;

import java.util.Random;

public class FaultTask implements Runnable {

    @Override
    public void run() {
        int result;
        Random random = new Random(Thread.currentThread().getId());
        while (true) {
            int chushu = ((int) (random.nextDouble() * 1000));
            result = 1000 / chushu;
            System.out.printf("%s : %d, %d\n", Thread.currentThread().getId(), chushu, result);
            if (Thread.currentThread().isInterrupted()) {
                System.out.printf("%d : Interrupted\n", Thread.currentThread().getId());
                return;
            }
        }
    }
}
