package com.getset.j7cc.chapter4;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 线程执行者的测试类。线程执行者将任务的创建与执行分离。
 * 使用执行者，你只要实现Runnable对象并将它们提交给执行者。执行者负责执行，实例化和运行这些线程。除了这些，它还可以
 * 使用线程池提高性能。当你提交一个任务给这个执行者，它试图使用线程池中的线程来执行任务，从而避免继续创建线程。
 */
public class ThreadExecutor {
    /**
     * 4.2 创建一个线程执行者
     * 4.3 创建一个大小固定的线程执行者
     * 使用<code>Executors.newCachedThreadPool</code>和<code>Executors.newFixedThreadPool</code>来创建线程执行者。
     * 线程执行者使用<code>execute</code>方法提交<code>Runnable</code>任务。
     */
    @Test
    public void testExecutorCreation() throws InterruptedException {
        Server server = new Server();
        for (int i = 0; i < 100; i++) {
            server.executeTask(new RunnableTask("RunnableTask" + String.format("%2d", i)));
        }
        while (true) {
            TimeUnit.SECONDS.sleep(1);
            server.showInfo();
            if (server.tryEndServer()) {
                break;
            }
        }
    }

    /**
     * 4.4 执行者执行返回结果的任务
     * submit()方法来提交FactorialCalculator任务给执行者。这个方法返回Future<Integer>对象来管理任务，
     * 并且最终获取它的结果。
     */
    @Test
    public void testCallableReturnValue() throws InterruptedException, ExecutionException {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        List<Future<Integer>> results = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Integer number = random.nextInt(10);
            /**
             * submit()方法来提交FactorialCalculator任务给执行者。这个方法返回Future<Integer>对象来管理任务，
             * 并且最终获取它的结果。
             */
            Future<Integer> result = executor.submit(new FactorialCalculator(number));
            results.add(result);
        }

        while (executor.getActiveCount() > 0) {
            TimeUnit.SECONDS.sleep(1);
            System.out.print("active: " + executor.getActiveCount() + "  completed: " + executor.getCompletedTaskCount() + "  status:");
            for (Future<Integer> future : results) {
                /**
                 * 可以控制任务的状态：可以取消任务，检查任务是否已经完成。
                 * 如使用isDone()方法来检查任务是否已经完成。
                 */
                System.out.print(" " + (future.isDone() ? 1 : 0));
            }
            System.out.println();
        }

        for (Future<Integer> future : results) {
            /**
             * 使用get()方法可以获取call()方法返回的结果。这个方法会等待，直到Callable对象完成call()方法的
             * 执行，并且返回它的结果。如果线程在get()方法上等待结果时被中断，它将抛出InterruptedException
             * 异常。如果call()方法抛出 异常，这个方法会抛出ExecutionException异常。
             */
            System.out.print("final result: " + future.get());
        }
        System.out.println();

        executor.shutdown();
    }

    /**
     * 4.5 运行多个任务并处理第一个结果
     * ThreadPoolExecutor类中的invokeAny()方法接收任务集合，并启动它们，返回完成时没有抛出异常的第一个任务的结果。
     * 该方法返回的数据类型与启动任务的call()方法返回的类型一样。
     */
    @Test
    public void testInvokeAny() throws ExecutionException, InterruptedException {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        List<CallableTask> tasks = new ArrayList<>();
        tasks.add(new CallableTask("Task-1"));
        tasks.add(new CallableTask("Task-2"));
        /**
         * 通过一个Collection的数据结构传递给 invokeAny 方法，返回结果是第一个执行结束的任务，另一个任务被中断。
         */
        Integer result = executor.invokeAny(tasks);
        System.out.println("The first finished task took " + result + " seconds.");
    }

    /**
     * 4.6 运行多个任务并处理所有的结果
     * ThreadPoolExecutor类中的invokeAll()方法接收任务集合，并启动它们，所有任务完成时返回结合集合。
     */
    @Test
    public void testInvokeAll() throws ExecutionException, InterruptedException {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        List<CallableTask> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tasks.add(new CallableTask("Task-" + i));
        }
        /**
         * 通过一个Collection的数据结构传递给 invokeAny 方法，返回所有任务执行结果的集合。
         */
        List<Future<Integer>> results = executor.invokeAll(tasks);

        for (Future<Integer> result :
                results) {
            System.out.print(" " + result.get());
        }
        System.out.println();
    }

    /**
     * 4.7 在延迟后执行者运行任务
     * 使用ScheduledThreadPoolExecutor安排任务在指定的时间后执行
     */
    @Test
    public void testScheduledExecutor() throws InterruptedException {
        ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(10);
        System.out.println("Main: Started.");
        for (int i = 0; i < 10; i++) {
            executor.schedule(new CallableTask("Task-" + i), i, TimeUnit.SECONDS);
        }

        /**
         * shutdown和awaitTermination为接口ExecutorService定义的两个方法，一般情况配合使用来关闭线程池。
         *
         * shutdown方法：平滑的关闭ExecutorService，当此方法被调用时，ExecutorService停止接收新的任务并且等待已经
         * 提交的任务（包含提交正在执行和提交未执行）执行完成。当所有提交任务执行完毕，线程池即被关闭。
         *
         * awaitTermination方法：接收人timeout和TimeUnit两个参数，用于设定超时时间及单位。当等待超过设定时间时，会
         * 监测ExecutorService是否已经关闭，若关闭则返回true，否则返回false。一般情况下会和shutdown方法组合使用。
         */
        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.SECONDS);

        System.out.println("Main: All finished.");
    }

    /**
     * 4.8 执行者定期的执行任务
     * 通过ScheduledThreadPoolExecutor类可以执行周期性任务
     */
    @Test
    public void testSchedulAtFixedRate() throws InterruptedException {
        // 使用ScheduledThreadPool
        ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(10);

        /**
         * 使用scheduleAtFixedRate方法，四个参数分别为“Runnable任务”、“开始延迟时间”、“周期间隔时间”、“时间单位”。
         * 如果任务执行时间比间隔时间长，则需要等上个周期执行完之后才会执行下一个周期。
         */

        ScheduledFuture<?> scheduled = executor.scheduleAtFixedRate(new RunnableTask("scheduled"), 1, 2, TimeUnit.SECONDS);

        for (int i = 0; i < 50; i++) {
            // 使用getDelay方法查询距离下次周期启动还有多久，如果任务执行时间比周期间隔长，则有可能是负值
            System.out.println("Main: Delay " + scheduled.getDelay(TimeUnit.SECONDS) + "  Active count: " + executor.getActiveCount());
            TimeUnit.SECONDS.sleep(1);
        }

        executor.shutdown();
    }

    /**
     * 4.9 执行者取消任务
     * 取消任务是通过调用Future的cancel方法实现的。
     * <p>
     * 4.10 执行者控制一个结束任务
     * FutureTask提供了done()方法用来定义任务执行完毕或被取消后的执行动作。
     * <p>
     * FutureTask实现了Future和Runnable两个接口，因此既可以submit给执行者，也可以用来作为Future使用
     */
    @Test
    public void testCancelAndDone() throws InterruptedException {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        MyFutureTask[] tasks = new MyFutureTask[10];
        for (int i = 0; i < 10; i++) {
            tasks[i] = new MyFutureTask(new CallableTask("Task-" + i));
            executor.submit(tasks[i]);
        }

        TimeUnit.SECONDS.sleep(1);
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            if (random.nextBoolean() && !tasks[i].isDone()) {
                // 取消任务
                tasks[i].cancel(true);
            }
        }

        executor.shutdown();
        executor.awaitTermination(20, TimeUnit.SECONDS);
    }

    /**
     * 4.11 执行者分离运行任务和处理结果
     * CompletionService类整合了Executor和BlockingQueue的功能。你可以将Callable任务提交给它去执行，然后使用类似于队列中的take方法获取线程的返回值。
     * 使用CompletionService来维护处理线程不的返回结果时，主线程总是能够拿到最先完成的任务的返回值，而不管它们加入线程池的顺序。
     */
    @Test
    public void testCompletionService() throws InterruptedException, ExecutionException {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        /**
         * CompletionService与ExecutorService最主要的区别在于：
         * 前者submit的task不一定是按照加入时的顺序完成的。CompletionService对ExecutorService进行了包装，内部维护一个保存Future对象的BlockingQueue。
         * 只有当这个Future对象状态是结束的时候，才会加入到这个Queue中，take()和poll()方法其实就是Producer-Consumer中的Consumer。
         * 它们会从Queue中取出Future对象，如果Queue是空的，take方法会阻塞在那里，直到有完成的Future对象加入到Queue中；poll方法会返回null。
         */
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);
        for (int i = 0; i < 10; i++) {
            completionService.submit(new CallableTask("Task-" + i));
        }

        /**
         * 以下两个while语句都可以用于取出执行结果。
         * 分别用 take() 方法和 poll() 方法，take方法会等待结果就绪；而poll方法会直接返回，如果没有就绪的结果返回null。
         */
//        while (executor.getActiveCount() > 0) {
//            System.out.println(completionService.take().get());
//        }

        while (executor.getActiveCount() > 0) {
            TimeUnit.SECONDS.sleep(1);
            Future<Integer> result;
            while ((result = completionService.poll()) != null) {
                System.out.println(result.get());
            }
        }

        executor.shutdown();
        executor.awaitTermination(50, TimeUnit.SECONDS);
    }

    /**
     * 4.12 执行者控制被拒绝的任务
     * 通过实现RejectedExecutionHandler，在执行者中管理拒绝任务。
     */
    @Test
    public void testRejectSubmit() throws InterruptedException {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        // 指定用于处理Rejected异常时的Handler
        executor.setRejectedExecutionHandler(new MyRejectedExceptionHandler());

        for (int i = 0; i < 10; i++) {
            executor.submit(new CallableTask("Task-" + i));
        }

        executor.shutdown();

        // 在shutdown()方法和执行者结束之间，提交任务给执行者，这个任务将被拒绝，因为执行者不再接收新的任务。
        executor.submit(new RunnableTask("TaskRejected"));

        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
}
