package com.getset.j7cc.chapter7;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

/**
 * 在ForkJoinPool中“干活”的线程们，自定义的线程类扩展了 ForkJoinWorkThread，增加了一个计数器用来统计自身承担的“工作量”。
 */
public class MyForkJoinWorkerThread extends ForkJoinWorkerThread {
    private static ThreadLocal<Integer> taskCounter = new ThreadLocal<>();

    protected MyForkJoinWorkerThread(ForkJoinPool pool) {
        super(pool);
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.printf("MyWorkerThread %d: Initializing task counter.\n", getId());
        taskCounter.set(0);
    }

    @Override
    protected void onTermination(Throwable exception) {
        System.out.printf("MyWorkerThread %d: %d.\n", getId(), taskCounter.get());
        super.onTermination(exception);
    }

    public void addTask() {
        taskCounter.set(taskCounter.get() + 1);
    }

}
