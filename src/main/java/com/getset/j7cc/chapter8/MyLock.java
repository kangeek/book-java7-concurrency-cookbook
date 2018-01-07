package com.getset.j7cc.chapter8;

import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

public class MyLock extends ReentrantLock {
    /**
     * 使用Lock类的保护方法 getOwner()， 返回控制锁的线程（如果存在）的名字。
     */
    public String getOwnerName() {
        if (getOwner() == null) {
            return "None";
        } else {
            return getOwner().getName();
        }
    }

    /**
     * 使用Lock类的保护方法 getQueuedThreads()，返回在锁里的线程的 queued
     */
    public Collection<Thread> getThreads() {
        return getQueuedThreads();
    }
}
