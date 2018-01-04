package com.getset.j7cc.chapter4;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
    private ThreadPoolExecutor executor;

    public Server() {
        /**
         * 在Server类的构造器中创建ThreadPoolExecutor。ThreadPoolExecutor有4个不同的构造器，但由于它们的
         * 复杂性，Java并发API提供Executors类来构造执行者和其他相关对象。即使我们可以通过ThreadPoolExecutor
         * 类的任意一个构造器来创建ThreadPoolExecutor，但这里推荐使用Executors类。
         *
         * 使用 newCachedThreadPool()方法创建一个缓存线程池。这个方法返回ExecutorService对象，所以它被转换
         * 为 ThreadPoolExecutor类型来访问它的所有方法。对已创建的缓存线程池，当需要执行新的任务会创建新的
         * 线程，如果它们已经完成运行任务，变成可用状态，会重新使用这些线程。线程重复利用的好处是，它减少线
         * 程创建的时间。缓存线程池的缺点是，为新任务不断创建线程， 所以如果你提交过多的任务给执行者，会使
         * 系统超载。
         */
//        this.executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        // 创建一个大小固定的线程执行者
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    }

    public void executeTask(RunnableTask runnableTask) {
//        System.out.println("Server : A new runnableTask has arrived");
        // 使用execute()方法提交Runnable或Callable类型的任务。
        executor.execute(runnableTask);
    }

    /**
     * ThreadPoolExecutor类提供了一些统计方法。
     */
    public synchronized void showInfo() {
        System.out.println("Server : Pool size: " + executor.getPoolSize() + "/" + executor.getLargestPoolSize());
        System.out.println("Server : Active count: " + executor.getActiveCount());
        System.out.println("Server : Completed Tasks: " + executor.getCompletedTaskCount());
    }

    public boolean tryEndServer() {
        /**
         * ThreadPoolExecutor 类和一般执行者的一个关键方面是，你必须明确地结束它。如果你没有这么做，这个执行者
         * 会继续它的执行，并且这个程序不会结束。如果执行者没有任务可执行， 它会继续等待新任务并且不会结束它的
         * 执行。一个Java应用程序将不会结束，除非所有的非守护线程完成它们的执行。所以，如果你不结束这个执行者，
         * 你的应用程序将不会结束。
         */
        if (executor.getActiveCount() == 0) {
            executor.shutdown();
            return true;
        }
        return false;
    }
}
