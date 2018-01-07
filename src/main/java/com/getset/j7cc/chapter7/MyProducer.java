package com.getset.j7cc.chapter7;

import java.io.RandomAccessFile;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

public class MyProducer implements Runnable {
    private TransferQueue<MyEvent> transferQueue;

    public MyProducer(TransferQueue<MyEvent> transferQueue) {
        this.transferQueue = transferQueue;
    }

    @Override
    public void run() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            try {
                transferQueue.put(new MyEvent(Thread.currentThread().getName(), random.nextInt(100)));
//                System.out.println("Producer: " + Thread.currentThread().getName() + " - " + i);
//                TimeUnit.MILLISECONDS.sleep(random.nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
