package com.getset.j7cc.chapter2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrintQueue {
//    private final Lock queueLock = new ReentrantLock();
    /**
     * true值将开启公平模式。在这个模式中，当有多个线程正在等待一把锁（ReentrantLock或者ReentrantReadWriteLock），
     * 这个锁必须选择它们中间的一个来获得进入临界区，它将选择等待时间最长的线程。
     */
    private final Lock queueLock = new ReentrantLock(false);

    public void printJob(Object document) {
        queueLock.lock();
        try {
            Long duration = (long) (Math.random() * 10000);
            System.out.println(Thread.currentThread().getName() + ": PrintQueue: Printing a Job during " + (duration / 1000) +
                    " seconds");
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }
    }

    public void printJobUsingFair(Object document) {
        printJob(document);
        printJob(document);
    }
}
