package com.getset.j7cc.chapter7;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

public class CustomizedConcurrenty {

    /**
     * 7.2 定制ThreadPoolExecutor类
     */
    @Test
    public void testCustomizedThreadPoolExecutor() throws ExecutionException, InterruptedException {
        List<Future<String>> results = new ArrayList<>();

        // 自定义的ThreadPoolExecutor，覆盖了一些方法来统计任务执行时长，并在关闭任务时输出统计信息
        MyThreadPoolExecutor executor = new MyThreadPoolExecutor(3, 5, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            Future<String> result = executor.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    int duration = 1000 + ThreadLocalRandom.current().nextInt(1000);
                    TimeUnit.MILLISECONDS.sleep(duration);
                    return finalI + " : " + duration + " : " + new Date().toString();
                }
            });
            results.add(result);
        }

        for (int i = 0; i < 10; i++) {
            results.get(i).get();
        }
    }

    /**
     * 7.3 实现一个优先级制的执行者类
     * 使用优先级队列来存储提交执行的任务。
     */
    @Test
    public void testPriorityTasks() throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 1000, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>());
        for (int i = 0; i < 10; i++) {
            executor.execute(new MyPriorityTask());
        }
        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.SECONDS);
    }

    /**
     * 7.4 实现ThreadFactory接口来生成自定义线程
     */
    @Test
    public void testCustomizedThreadFactory() throws InterruptedException {
        MyThreadFactory factory = new MyThreadFactory("mythread");
        Thread thread = factory.newThread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        thread.join();
        System.out.println(thread);
    }

    /**
     * 7.5 在执行者对象中使用我们的 ThreadFactory
     * 将ThreadFactory作为参数用于构造Executor，则后者会基于该ThreadFactory生成线程。
     */
    @Test
    public void testCustomizedThreadFactoryInExecutor() throws InterruptedException {
        MyThreadFactory factory = new MyThreadFactory("mythread");
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool(factory);
        executor.submit(task);
        executor.submit(task);
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }


    /**
     * 7.6 在计划好的线程池中定制运行任务
     * 实现自定义的 RunnableScheduledFuture 接口来执行延迟和周期性任务，任务执行前后输出一些信息。
     */
    @Test
    public void testCustomizedScheduled() throws InterruptedException {
        MyScheduledThreadPoolExecutor executor = new MyScheduledThreadPoolExecutor(2);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                System.out.println("Task start...");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Task ends...");
            }
        };
        executor.schedule(task, 1, TimeUnit.SECONDS);

        TimeUnit.SECONDS.sleep(4);

        executor.scheduleAtFixedRate(task, 1, 3, TimeUnit.SECONDS);

        TimeUnit.SECONDS.sleep(10);

        executor.shutdown();
        executor.awaitTermination(20, TimeUnit.SECONDS);
    }

    /**
     * 7.7 实现ThreadFactory接口来生成自定义线程给Fork/Join框架
     * 一个在ForkJoinPool类中使用的自定义的ForkJoinWorkerThread（用来统计自己承担的工作数量），
     * WorkThread用自定义的工厂传递给ForkJoinPool。
     */
    @Test
    public void testCustomedForkJoinFactoryAndWorker() throws InterruptedException, ExecutionException {
        // 自定义的线程工厂
        MyForkJoinWorkerThreadFactory factory = new MyForkJoinWorkerThreadFactory();
        ForkJoinPool pool = new ForkJoinPool(4, factory, null, false);
        int array[] = new int[100000];
        for (int i = 0; i < array.length; i++) {
            array[i] = 1;
        }

        MyRecursiveTask task = new MyRecursiveTask(array, 0, array.length - 1);
        pool.execute(task);
        task.join();
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.HOURS);
        System.out.println(task.get());
    }

    /**
     * 7.8 在Fork/Join框架中定制运行任务
     * 为 Fork/Join 框架加入定制化的任务，扩展ForkJoinTask类。定制化的任务可以计量运行时间并写入控制台台，从而
     * 可以控制它的进展。当然也可以实现你自己的 Fork/Join 任务来写日志信息，为了获得在这个任务中使用的资源，或
     * 者来 post-process 任务的结果。
     */
    @Test
    public void testCustomedJorkJoinTask() throws InterruptedException {
        // 扩展自定义任务
        class Task extends MyForkJoinTask {
            private int[] array;
            private int start;
            private int end;

            public Task(String name, int[] array, int start, int end) {
                super(name);
                this.array = array;
                this.start = start;
                this.end = end;
            }

            @Override
            protected void compute() {
                if (end - start > 100) {
                    int mid = (end + start) / 2;
                    Task task1 = new Task(this.getName() + "1", array, start, mid);
                    Task task2 = new Task(this.getName() + "2", array, mid, end);
                    invokeAll(task1, task2);
                } else {
                    for (int i = start; i < end; i++) {
                        array[i]++;
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        int[] array = new int[100000];
        for (int i = 0; i < array.length; i++) {
            array[i] = 1;
        }

        ForkJoinPool pool = new ForkJoinPool(10);
        pool.execute(new Task("mytask", array, 0, array.length));
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.HOURS);

        for (int i = 0; i < 100; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }

    /**
     * 7.9 实现一个自定义锁类
     * 自定义实现Lock对象，它实现了Lock接口并可用来保护临界区的类。
     *
     * @throws InterruptedException
     */
    @Test
    public void testCustomedLock() throws InterruptedException {
        class Task implements Runnable {
            private Lock lock;
            private String name;

            public Task(String name, Lock lock) {
                this.lock = lock;
                this.name = name;
            }

            @Override
            public void run() {
                lock.lock();
                System.out.println("Thread " + name + " take the lock");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
                System.out.println("Thread " + name + " free the lock");
            }
        }

        Thread[] threads = new Thread[10];
        Lock lock = new MyLock();
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new Task("Task-" + i, lock));
            threads[i].start();
        }
        for (int i = 0; i < 10; i++) {
            threads[i].join();
        }
    }


    /**
     * 7.10 实现一个基于优先级传输Queue
     * 实现在 producer/ consumer 问题中使用的数据结构，这些元素将被按照他们的优先级排序，级别高的会先被消费。
     */
    @Test
    public void testCustomizedTransferQueue() throws InterruptedException {
        TransferQueue<MyEvent> transferQueue = new MyPriorityTransferQueue<>();
        Thread[] producers = new Thread[10];
        for (int i = 0; i < 10; i++) {
            producers[i] = new Thread(new MyProducer(transferQueue));
            producers[i].start();
        }
        Thread consumer = new Thread(new MyConsumer(transferQueue));
        consumer.start();

        System.out.println("Main: waiting consumer count: " + transferQueue.getWaitingConsumerCount());
        transferQueue.transfer(new MyEvent("main", 6));
        System.out.println("Main: first manually event transferred.");

        for (int i = 0; i < 10; i++) {
            producers[i].join();
        }

        TimeUnit.SECONDS.sleep(1);

        System.out.println("Main: waiting consumer count: " + transferQueue.getWaitingConsumerCount());
        transferQueue.transfer(new MyEvent("main", 4));
        System.out.println("Main: second manually event transferred.");

        consumer.join();
    }

    /**
     * 7.11 实现你自己的原子对象
     * 通过扩展 AtomicInteger 实现了自定义的 AtomicInteger， 可以记录“Compare and Set”的失败次数。
     */
    @Test
    public void testCustomizeAtomic() throws InterruptedException {
        final MyAtomicInteger myAtomicInteger = new MyAtomicInteger();
        Thread[] incs = new Thread[10];
        Thread[] decs = new Thread[10];
        for (int i = 0; i < incs.length; i++) {
            incs[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10000; j++) {
                        myAtomicInteger.inc();
                    }
                }
            });
            incs[i].start();
            decs[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10000; j++) {
                        myAtomicInteger.dec();
                    }
                }
            });
            decs[i].start();
        }

        for (int i = 0; i < 10; i++) {
            incs[i].join();
            decs[i].join();
        }

        System.out.println(myAtomicInteger.get() + " with " + myAtomicInteger.getFailureCount() + " failed tries.");
    }

}
