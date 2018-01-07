package com.getset.j7cc.chapter7;

import java.util.Date;
import java.util.concurrent.*;

public class MyScheduledTask<V> extends FutureTask<V> implements RunnableScheduledFuture<V> {
    private RunnableScheduledFuture<V> task;
    private ScheduledThreadPoolExecutor executor;
    private long period;
    private long startDate;

    /**
     * 实现类的构造函数。它接收任务：将要运行的 Runnable 对象，任务要返回的 result，将被用来创建 MyScheduledTask
     * 对象的 RunnableScheduledFuture 任务，和要执行这个任务的 ScheduledThreadPoolExecutor 对象。 调用它的父类的
     * 构造函数并储存任务和执行者属性。
     */
    public MyScheduledTask(Runnable runnable, V result, RunnableScheduledFuture<V> task, ScheduledThreadPoolExecutor executor) {
        super(runnable, result);
        this.task = task;
        this.executor = executor;
    }

    @Override
    public boolean isPeriodic() {
        return task.isPeriodic();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        if (!isPeriodic()) {
            return task.getDelay(unit);
        } else {
            if (startDate == 0) {
                return task.getDelay(unit);
            } else {
                return unit.convert(startDate - System.currentTimeMillis(), unit);
            }
        }
    }

    @Override
    public int compareTo(Delayed o) {
        return task.compareTo(o);
    }

    /**
     * 实现方法 run()。如果这是一个周期性任务，你要用下一个执行任务的开始日期更新它的 startDate 属性。用当前
     * 日期和时间间隔的和计算它。 然后，把再次把任务添加到 ScheduledThreadPoolExecutor 对象的 queue中。
     */
    @Override
    public void run() {
        if (isPeriodic() && (!executor.isShutdown())) {
            startDate = new Date().getTime() + period;
            executor.getQueue().add(this);
        }
        System.out.printf("Pre-MyScheduledTask: %s\n",new Date());
        System.out.printf("MyScheduledTask: Is Periodic:%s\n",isPeriodic());
        super.runAndReset();
        System.out.printf("Post-MyScheduledTask: %s\n",new Date());
    }

    public void setPeriod(long period) {
        this.period = period;
    }
}
