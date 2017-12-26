package com.getset.j7cc.chapter3;

import java.util.concurrent.CountDownLatch;

public class VideoConference implements Runnable {
    private final CountDownLatch countDownLatch;

    public VideoConference(int number) {
        // 初始化的时候指定countdown初始值
        this.countDownLatch = new CountDownLatch(number);
    }

    public void linkedIn(String name) {
        // 计数
        countDownLatch.countDown();
        System.out.println(name + " has linked in. Waiting for the rest " + countDownLatch.getCount() + " participants.");
    }

    @Override
    public void run() {
        try {
            // 在countDownLatch的计数值减为0之前会一直wait
            countDownLatch.await();
            System.out.println("All the paticipants have arrived, let's begin.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
