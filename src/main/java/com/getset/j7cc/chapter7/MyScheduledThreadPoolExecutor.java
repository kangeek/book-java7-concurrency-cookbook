package com.getset.j7cc.chapter7;

import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {
    public MyScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    /**
     * 这里让自定义的 Executor 使用自定义的 MySchedulerTask
     */
    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
        return new MyScheduledTask<>(runnable, null, task, this);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        ScheduledFuture task = super.scheduleAtFixedRate(command, initialDelay, period, unit);
        MyScheduledTask myScheduledTask = (MyScheduledTask)task;
        myScheduledTask.setPeriod(TimeUnit.MILLISECONDS.convert(period, unit));
        return myScheduledTask;
    }
}
