package com.getset.j7cc.chapter7;

import java.util.concurrent.TransferQueue;

public class MyConsumer implements Runnable {
    private TransferQueue<MyEvent> transferQueue;

    public MyConsumer(TransferQueue<MyEvent> transferQueue) {
        this.transferQueue = transferQueue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1002; i++) {
            try {
                MyEvent event = transferQueue.take();
                System.out.println("Consumer: " + event.getThread() + " - " + event.getPriority());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
