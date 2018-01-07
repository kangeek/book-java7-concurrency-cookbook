package com.getset.j7cc.chapter8;

import edu.umd.cs.mtc.MultithreadedTestCase;

import java.util.concurrent.LinkedTransferQueue;

public class ProducerConsumerTest extends MultithreadedTestCase {
    private LinkedTransferQueue<String> queue;

    @Override
    public void initialize() {
        super.initialize();
        queue = new LinkedTransferQueue<>();
        System.out.println("The test has been initialized.");
    }

    // 实现的逻辑是第一个consumer。调用 queue 的 take() 方法，然后把返回值写入操控台。
    public void thread1() throws InterruptedException {
        System.out.println("Thread1 take " + queue.take());
    }

    // 实现的逻辑是第二个consumer。首先，使用 waitForTick() 方法，一直等到第一个线程在
    // take() 方法中进入休眠。然后，调用queue的 take() 方法，并把返回值写入操控台。
    public void thread2() throws InterruptedException {
        waitForTick(1);
        System.out.println("Thread2 take " + queue.take());
    }

    // 实现的逻辑是producer。 首先，使用 waitForTick() 两次一直等到2个consumers被阻塞。
    // 然后，调用 queue的 put() 方法插入2个String 到queue中。
    public void thread3() {
        waitForTick(2);
        queue.put("element1");
        queue.put("element2");
        System.out.println("Thread3 produce two elements.");
    }

    @Override
    public void finish() {
        super.finish();
        System.out.println("Test ended.");
        assertEquals(queue.size(), 0);
        System.out.println("The queue is empty.");
    }
}
