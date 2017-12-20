package com.getset.j7cc.chapter1;

import org.junit.Test;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

public class ThreadManagement {
    /**
     * 1.2 线程的创建和运行
     * 构建一个实现Runnable接口的类, 然后创建一个thread类对象并传递Runnable对象作为构造参数。
     * 它能创建和运行10个线程。每一个线程能计算和输出1-10以内的乘法表。
     */
    @Test
    public void testCreateThread() {
        for (int i = 0; i < 10; i++) {
            Calculator calculator = new Calculator(i);
            Thread thread = new Thread(calculator);
            thread.start();
        }
    }

    /**
     * 1.3 获取和设置线程信息
     * 为10个线程设置名字和优先级的程序，然后展示它们的状态信息直到线程结束。这些线程会计算数字乘法表。
     */
    @Test
    public void testGetAndSetThreadInfo() {
        Thread threads[] = new Thread[10];
        Thread.State status[] = new Thread.State[10];

        try (PrintWriter pw = new PrintWriter(new FileWriter("target/calc.log"))) {
            for (int i = 0; i < 10; i++) {
                threads[i] = new Thread(new Calculator(i));
                if (i % 2 == 0) {
                    threads[i].setPriority(Thread.MAX_PRIORITY);
                } else {
                    threads[i].setPriority(Thread.MIN_PRIORITY);
                }
                threads[i].setName("Thread-" + i);

                status[i] = threads[i].getState();
                pw.println("Main: Status of Thread-" + i + ": " + status[i]);
            }

            for (int i = 0; i < 10; i++) {
                threads[i].start();
            }

            boolean finish = false;
            while (!finish) {
                for (int i = 0; i < 10; i++) {
                    if (threads[i].getState() != status[i]) {
                        writeThreadInfo(pw, threads[i], status[i]);
                        status[i] = threads[i].getState();
                    }
                }
                finish = true;
                for (int i = 0; i < 10; i++) {
                    finish = finish && (threads[i].getState() == Thread.State.TERMINATED);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeThreadInfo(PrintWriter pw, Thread thread, Thread.State state) {
        pw.printf("Main : Id %d - %s\n",thread.getId(),thread.getName());
        pw.printf("Main : Priority: %d\n",thread.getPriority());
        pw.printf("Main : Old State: %s\n",state);
        pw.printf("Main : New State: %s\n",thread.getState());
        pw.printf("Main : ************************************\n");
    }

    /**
     * 1.4 线程的中断
     * 创建线程，然后在1秒之后，它会使用中断机制来强制结束线程。
     */
    @Test
    public void testInterrupt() {
        Thread task = new PrimeGenerator();
        task.start();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        task.interrupt();
    }

    /**
     * 1.5 操作线程的中断机制
     * 根据给定的名称在文件件和子文件夹里查找文件，这个将展示如何使用InterruptedException异常来控制线程的中断。
     */
    @Test
    public void testInterrupt2() {
        FileSearch searcher = new FileSearch("/home/kang", ".gitignore");
        Thread task = new Thread(searcher);
        task.start();

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        task.interrupt();
    }

    /**
     * 1.6 线程的睡眠和恢复
     * 开发一个程序使用sleep()方法来每秒写入真实的日期。
     * 当 Thread 是睡眠和中断的时候，那方法会立刻抛出InterruptedException异常并不会一直等到睡眠时间过去。
     */
    @Test
    public void testSleep() {
        Thread task = new Thread(new PrintClock());
        task.start();

        try {
            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        task.interrupt();
    }

    /**
     * 1.7 等待线程的终结
     * 使用join()方法等待其他线程的结束。
     */
    @Test
    public void testJoin() {
        Thread thread1 = new Thread(new DataSourcesLoader());
        Thread thread2 = new Thread(new NetworkConncetionsLoader());
        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Main: Configuration has been loaded.");
    }

    /**
     * 1.8 守护线程的创建和运行
     * 演示如何创建一个守护线程，开发一个用2个线程的例子；我们的使用线程会写事件到queue, 守护线程会清除queue里10秒前创建的事件。
     */
    @Test
    public void testDaemon() throws InterruptedException {
        Deque<Event> queue = new ArrayDeque<>();
        WriterTask writerTask = new WriterTask(queue);
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(writerTask);
            thread.start();
        }
        CleanerTask cleaner = new CleanerTask(queue);
        cleaner.start();

        TimeUnit.SECONDS.sleep(100);
    }

    /**
     * 1.9 处理线程的不受控制异常
     * 演示如何捕获和处理线程对象抛出的未检测异常来避免程序终结。
     */
    @Test
    public void testExceptionHandler() throws InterruptedException {
        Thread task = new Thread(new DangrousTask());
        task.setUncaughtExceptionHandler(new ExceptionHandler());
        task.start();
        TimeUnit.SECONDS.sleep(2);
        System.out.println("不影响后续的操作...");
    }

    /**
     * 1.10 使用本地线程变量
     * 以下程序用来描述在第一段话里的问题，和另一个程序使用本地线程变量机制解决这个问题。
     */
    @Test
    public void testLocalVariable() throws InterruptedException {
//        UnsafeTask task = new UnsafeTask();
        SafeTask task = new SafeTask();
        for (int i = 0; i < 10; i++) {
            new Thread(task).start();
        }

        TimeUnit.SECONDS.sleep(2);
    }

    /**
     * 1.11 线程组
     * 将开发一个简单的例子来演示 ThreadGroup 对象。我们有 10 个随机时间休眠的线程 (例如，模拟搜索)，然后当其中一个完成，就中断其余的。
     */
    @Test
    public void testThreadGroup() {
        ThreadGroup threadGroup = new ThreadGroup("Searcher");
        ThreadGroupResult result = new ThreadGroupResult();
        SearchTask searchTask = new SearchTask(result);

        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(threadGroup, searchTask);
            thread.start();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Number of Threads: %d\n",threadGroup.activeCount());
        System.out.printf("Information about the Thread Group\n");
        // 使用list() 方法写关于 ThreadGroup ob对象信息
        threadGroup.list();

        // 使用 activeCount() 和 enumerate() 方法来获取线程个数和与ThreadGroup对象关联的线程的列表。我们可以用这个方法来获取信息， 例如，每个线程状态。
        Thread[] threads = new Thread[threadGroup.activeCount()];
        threadGroup.enumerate(threads);
        for (int i = 0; i < threadGroup.activeCount(); i++) {
            System.out.printf("Thread %s: %s\n",threads[i].getName(),threads[i].getState());
        }
        waitFinish(threadGroup);
        // 用interrupt() 方法中断组里的线程
        threadGroup.interrupt();
    }

    private static void waitFinish(ThreadGroup threadGroup) {
        while (threadGroup.activeCount() > 4) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 1.12 处理线程组内的不受控制异常
     * 捕获所有被ThreadGroup类的任何线程抛出的非捕捉异常。
     */
    @Test
    public void testThreadGroupException() throws InterruptedException {
        MyThreadGroup threadGroup = new MyThreadGroup("myThreadGroup");
        FaultTask task = new FaultTask();
        for (int i = 0; i < 2; i++) {
            Thread t = new Thread(threadGroup, task);
            t.start();
        }
        TimeUnit.SECONDS.sleep(5);
    }

    /**
     * 1.13 用线程工厂创建线程
     * 使用 ThreadFactory 接口来创建Thread 对象
     */
    @Test
    public void testThreadFactory() throws InterruptedException {
        MyThreadFactory factory = new MyThreadFactory("hello");
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
        for (int i = 0; i < 10; i++) {
            factory.newThread(task).start();
        }

        System.out.println(factory.getStats());
    }
}
