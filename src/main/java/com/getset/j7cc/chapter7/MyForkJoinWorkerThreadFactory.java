package com.getset.j7cc.chapter7;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

/**
 * 自定义的ForkJoin线程工厂，为ForkJoin提供工作线程。
 */
public class MyForkJoinWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
    @Override
    public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
        return new MyForkJoinWorkerThread(pool);
    }
}
