package com.getset.j7cc.chapter8;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class TaskForLock implements Runnable {
    private Lock lock;

    public TaskForLock(Lock lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            lock.lock();
            System.out.println(Thread.currentThread().getName() + " get the lock.");
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}
