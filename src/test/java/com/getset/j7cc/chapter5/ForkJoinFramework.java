package com.getset.j7cc.chapter5;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

public class ForkJoinFramework {

    /**
     * 5.2 创建 Fork/Join 池
     * 本例演示了任务的拆分执行，被拆分的任务并不需要返回结果，因此继承RecursiveAction即可。
     *
     * 5.5 任务中抛出异常
     * compute方法中不能抛出须检查异常，必须在方法内处理；
     * 对于非检查异常，不会在控制台输出，也不会中断整体执行，可以通过isCompletedAbnormally方法
     * 判断是否执行过程中存在异常。
     */
    @Test
    public void testForkJoin() throws InterruptedException {
        class Task extends RecursiveAction {
            private int[] intList;
            private int first;
            private int last;

            public Task(int[] intList, int first, int last) {
                this.intList = intList;
                this.first = first;
                this.last = last;
            }

            @Override
            protected void compute() {
                // 分而治之，超过10个就拆分为两个任务
                if (last - first > 10) {
                    int middle = (first + last) / 2;
                    Task task1 = new Task(intList, first, middle);
                    Task task2 = new Task(intList, middle, last);
                    /**
                     * 调用invokeAll()方法，执行每个任务所创建的子任务。这是一个同步调用，这个任务在子任务结束之后
                     * 继续（可能完成）执行。在等待子任务结束之前，它所在的线程会去执行其他正在等待的任务。所以，
                     * Fork/Join框架比Runnable和Callable对象本身提供一种更高效的任务管理。
                     */
                    invokeAll(task1, task2);
                } else {
                    Random random = new Random();
                    System.out.println(">>> List from " + first + " to " + last);
                    for (int i = first; i < last; i++) {
                        // 这里抛出一个非检查异常
                        int d = 100 / (i - 50);

                        int value = random.nextInt(1000);
                        intList[i] = value;
                        try {
                            TimeUnit.MILLISECONDS.sleep(value);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        int[] intList = new int[100];
        Task task = new Task(intList, 0, 100);
        // 无参构造器会创建一个线程数等于CPU数的线程池
        ForkJoinPool pool = new ForkJoinPool();
        pool.execute(task);

        while (!task.isDone()) {
            System.out.println("[active: " + pool.getActiveThreadCount() +
                    ", size: " + pool.getPoolSize() +
                    /**
                     * 线程池的每个线程都有一个任务队列，如果有的线程早早完成了的话，那么可能会从
                     * 其他线程的队列中“偷”过任务来执行，称为work-stealing。
                     * getStealCount方法可以用来衡量多线程的任务分派是否合理。数字太大说明分派不均
                     * 匀，数字太小，则最好考虑不使用work-stealing，因为“偷”的过程也有一定的开销。
                     */
                    ", stealCount: " + pool.getStealCount() +
                    ", parallelism: " + pool.getParallelism() + "]");
            TimeUnit.MILLISECONDS.sleep(10);
        }

        if (task.isCompletedAbnormally()) {
            System.out.println("任务执行异常：" + task.getException());
        }

        pool.shutdown();

        for (int i = 0; i < 100; i++) {
            System.out.print(" " + intList[i]);
        }
    }

    /**
     * 5.3 加入任务的结果
     * 本例统计一个document（二维数组模拟）中某个word出现的次数。将总任务拆分为多个DocumentSearchTask，
     * 每个DocumentSearchTask又进一步按行拆分为LineSearchTask，后者又进一步拆分。
     * 由于需要统计个数，因此是返回结果的任务，所以任务类继承RecursiveTask，RecursiveTask实现了Future，
     * 因此可以通过get()方法返回结果。
     * 对于DocumentSearchTask和LineSearchTask的构造方法的最后一个参数设置为true，使用同步方式。
     *
     * 5.4 异步运行任务
     * 异步方式下，任务使用fork方法将自己提交给线程池，并立即返回，使用join方法获取执行结果。
     * 对于DocumentSearchTask和LineSearchTask的构造方法的最后一个参数设置为true，使用异步方式。
     */
    @Test
    public void testForkJoinResult() throws InterruptedException, ExecutionException {
        String wordToSearch = "hello";

        String[][] document = MockDocument.generateDocument(100, 1000,wordToSearch);
        DocumentSearchTask task = new DocumentSearchTask(document, 0, 100, wordToSearch, true);

        ForkJoinPool pool = new ForkJoinPool();
        pool.execute(task);

        while (!task.isDone()) {
            System.out.println("[active: " + pool.getActiveThreadCount() +
                    ", size: " + pool.getPoolSize() +
                    ", stealCount: " + pool.getStealCount() +
                    ", parallelism: " + pool.getParallelism() + "]");
            TimeUnit.MILLISECONDS.sleep(10);
        }

        pool.shutdown();

        System.out.println("Fount " + task.get() + " [hello].");
    }
}
