package com.getset.j7cc.chapter7;

import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * 覆盖ThreadPoolExecutor类的一些方法，计算你在执行者中执行的任务的执行时间，并且将关于执行者完成它的执行的统计信息写入到控制台。
 */
public class MyThreadPoolExecutor extends ThreadPoolExecutor {
    private ConcurrentHashMap<String, Date> startTimes;

    public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        startTimes = new ConcurrentHashMap<>();
    }

    /**
     * 覆盖beforeExecute()方法。写入一条信息（将要执行任务的线程名和任务的哈希编码）到控制台。
     * 在HashMap中，使用这个任务的哈希编码作为key，存储开始日期。
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        System.out.printf("MyExecutor: A task is beginning: %s : %s\n", t.getName(), r.hashCode());
        startTimes.put(String.valueOf(r.hashCode()), new Date());
    }

    /**
     * 覆盖afterExecute()方法。将任务的结果和计算任务的运行时间（将当前时间减去存储在HashMap中的
     * 任务的开始时间）的信息写入到控制台。
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        Future<?> result=(Future<?>)r;
        try {
            System.out.printf("*********************************\n");
            System.out.printf("MyExecutor: A task is finishing.\n");
            System.out.printf("MyExecutor: Result: %s\n",result.get());
            Date startDate=startTimes.remove(String.valueOf(r.
                    hashCode()));
            Date finishDate=new Date();
            long diff=finishDate.getTime()-startDate.getTime();
            System.out.printf("MyExecutor: Duration: %d\n",diff);
            System.out.printf("*********************************\n");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 覆盖shutdown()方法。将关于已执行的任务，正在运行的任务和待处理的任务信息写入到控制台。
     * 然后，使用super关键字调用父类的shutdown()方法。
     */
    @Override
    public void shutdown() {
        System.out.printf("MyExecutor: Going to shutdown.\n");
        System.out.printf("MyExecutor: Executed tasks: %d\n", getCompletedTaskCount());
        System.out.printf("MyExecutor: Running tasks: %d\n", getActiveCount());
        System.out.printf("MyExecutor: Pending tasks: %d\n", getQueue().size());
        super.shutdown();
    }

    /**
     * 覆盖shutdownNow()方法。将关于已执行的任务，正在运行的任务和待处理的任务信息写入到控制台。
     * 然后，使用super关键字调用父类的shutdownNow()方法。
     */
    @Override
    public List<Runnable> shutdownNow() {
        System.out.printf("MyExecutor: Going to immediately shutdown.\n");
        System.out.printf("MyExecutor: Executed tasks: %d\n", getCompletedTaskCount());
        System.out.printf("MyExecutor: Running tasks: %d\n", getActiveCount());
        System.out.printf("MyExecutor: Pending tasks: %d\n", getQueue().size());
        return super.shutdownNow();
    }
}
