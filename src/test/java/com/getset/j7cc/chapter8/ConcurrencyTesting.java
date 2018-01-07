package com.getset.j7cc.chapter8;

import edu.umd.cs.mtc.TestFramework;
import org.junit.Test;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConcurrencyTesting {

    /**
     * 8.2 监控锁接口
     * 从Lock对象可以获取的信息和如何获取这些信息。
     */
    @Test
    public void testMonitoringLock() throws InterruptedException {
        MyLock lock = new MyLock();
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new TaskForLock(lock), "Thread-" + i);
            threads[i].start();
        }

        for (int i = 0; i < 20; i++) {
            System.out.println("Lock owner: " + lock.getOwnerName());
            System.out.println("Queued thread: " + lock.getThreads().size());
            if (lock.hasQueuedThreads()) {
                Collection<Thread> lockedThreads = lock.getThreads();
                for (Thread t :
                        lockedThreads) {
                    System.out.print(t.getName() + " ");
                }
                System.out.println();
            }
            System.out.println("Lock fairness: " + lock.isFair());
            System.out.println("Lock islocked: " + lock.isLocked());

            TimeUnit.SECONDS.sleep(1);
        }

        for (int i = 0; i < 10; i++) {
            threads[i].join();
        }
    }

    /**
     * 8.3 监控Phaser类
     * 学习如何从Phaser类获取其状态信息。
     */
    @Test
    public void testMonitoringPhaser() throws InterruptedException {
        Phaser phaser = new Phaser(3);

        for (int i = 0; i < 3; i++) {
            TaskForPhase task = new TaskForPhase(i + 1, phaser);
            Thread thread = new Thread(task);
            thread.start();
        }

        for (int i = 0; i < 10; i++) {

            System.out.printf("********************\n");
            System.out.printf("Main: Phaser Log\n");
            System.out.printf("Main: Phaser: Phase: %d\n", phaser.getPhase());
            System.out.printf("Main: Phaser: Registered Parties:%d\n", phaser.getRegisteredParties());
            System.out.printf("Main: Phaser: Arrived Parties:%d\n", phaser.getArrivedParties());
            System.out.printf("Main: Phaser: Unarrived Parties:%d\n", phaser.getUnarrivedParties());
            System.out.printf("********************\n");

            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * 8.4 监控执行者框架
     * 从ThreadPoolExecutor执行者可以获取的信息和如何获取这些信息。
     */
    @Test
    public void testMonitorExecutor() throws InterruptedException {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            TaskForExecutor task = new TaskForExecutor(random.nextInt(10000));
            executor.submit(task);
        }

        for (int i = 0; i < 5; i++) {
            showExecutorLog(executor);
            TimeUnit.SECONDS.sleep(1);
        }

        executor.shutdown();

        for (int i = 0; i < 5; i++) {
            showExecutorLog(executor);
            TimeUnit.SECONDS.sleep(1);
        }

        executor.awaitTermination(1, TimeUnit.DAYS);

        System.out.printf("Main: End of the program.\n");
    }

    private void showExecutorLog(ThreadPoolExecutor executor) {
        System.out.printf("*********************");
        System.out.printf("Main: Executor Log");
        System.out.printf("Main: Executor: Core Pool Size:%d\n", executor.getCorePoolSize());
        System.out.printf("Main: Executor: Pool Size: %d\n", executor.getPoolSize());
        System.out.printf("Main: Executor: Active Count:%d\n", executor.getActiveCount());
        System.out.printf("Main: Executor: TaskForForkJoin Count: %d\n", executor.getTaskCount());
        System.out.printf("Main: Executor: Completed TaskForForkJoin Count:%d\n", executor.getCompletedTaskCount());
        System.out.printf("Main: Executor: Shutdown: %s\n", executor.isShutdown());
        System.out.printf("Main: Executor: Terminating:%s\n", executor.isTerminating());
        System.out.printf("Main: Executor: Terminated: %s\n", executor.isTerminated());
        System.out.printf("*********************\n");
    }

    /**
     * 8.5 监控Fork/Join池
     * 学习从ForkJoinPool类可以获取的信息和如何获取这些信息。
     */
    @Test
    public void testMonitorForkJoin() throws InterruptedException {
        ForkJoinPool pool = new ForkJoinPool();

        int array[] = new int[10000];

        TaskForForkJoin task = new TaskForForkJoin(array, 0, array.length);

        pool.execute(task);

        while (!task.isDone()) {
            showForJoinPoolLog(pool);
            TimeUnit.SECONDS.sleep(1);
        }

        pool.shutdown();

        pool.awaitTermination(1, TimeUnit.DAYS);

        showForJoinPoolLog(pool);
        System.out.printf("Main: End of the program.\n");
    }

    private void showForJoinPoolLog(ForkJoinPool pool) {
        System.out.printf("**********************\n");
        System.out.printf("Main: Fork/Join Pool log\n");
        System.out.printf("Main: Fork/Join Pool: Parallelism:%d\n",
                pool.getParallelism());
        System.out.printf("Main: Fork/Join Pool: Pool Size:%d\n",
                pool.getPoolSize());
        System.out.printf("Main: Fork/Join Pool: Active Thread Count:%d\n",
                pool.getActiveThreadCount());
        System.out.printf("Main: Fork/Join Pool: Running Thread Count:%d\n",
                pool.getRunningThreadCount());
        System.out.printf("Main: Fork/Join Pool: Queued Submission:%d\n",
                pool.getQueuedSubmissionCount());
        System.out.printf("Main: Fork/Join Pool: Queued Tasks:%d\n",
                pool.getQueuedTaskCount());
        System.out.printf("Main: Fork/Join Pool: Queued Submissions:%s\n",
                pool.hasQueuedSubmissions());
        System.out.printf("Main: Fork/Join Pool: Steal Count:%d\n",
                pool.getStealCount());
        System.out.printf("Main: Fork/Join Pool: Terminated :%s\n",
                pool.isTerminated());
        System.out.printf("**********************\n");
    }

    /**
     * 8.6 编写有效的日志
     * 学习如何使用 java.util.logging 包提供的类来添加一个log系统到并发应用。
     */
    @Test
    public void testLogging() {
        Logger logger = MyLogger.getLogger("Core");
//        Logger logger = Logger.getLogger("Main");

        logger.entering("Core", "main()");

        Thread threads[] = new Thread[5];

        for (int i = 0; i < threads.length; i++) {
            logger.log(Level.INFO, "Launching thread: " + i);
            TaskForLogging task = new TaskForLogging();
            threads[i] = new Thread(task);
            logger.log(Level.INFO, "Thread created: " + threads[i].getName());
            threads[i].start();
        }

        logger.log(Level.INFO, "Ten Threads created." + "Waiting for its finalization");

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
                logger.log(Level.INFO, "Thread has finished its execution", threads[i]);
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Exception", e);
            }
        }

        logger.exiting("Core", "main()");
    }

    /**
     * 8.10 MultithreadedTC测试并发代码
     * 使用 MultithreadedTC 库来为LinkedTransferQueue 实现一个测试。
     */
    @Test
    public void testMultithreadTC() throws Throwable {
        ProducerConsumerTest test = new ProducerConsumerTest();
        TestFramework.runOnce(test);
    }
}

