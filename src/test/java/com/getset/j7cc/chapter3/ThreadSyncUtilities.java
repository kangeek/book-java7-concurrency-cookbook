package com.getset.j7cc.chapter3;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

public class ThreadSyncUtilities {

    /**
     * 3.2 控制并发访问一个资源
     * 使用Semaphore类来实现一种比较特殊的semaphores种类，称为binary semaphores。
     * 这个semaphores种类保护访问共享资源的独特性，所以semaphore的内部计数器的值只
     * 能是1或者0。
     * <p>
     * 为了展示如何使用它，实现一个PrintQueue类来让并发任务打印它们的任务。这个PrintQueue
     * 类会受到binary semaphore的保护，所以每次只能有一个线程可以打印。
     */
    @Test
    public void testBinarySemaphore() throws InterruptedException {
        final PrintQueue printQueue = new PrintQueue();
        /**
         * 当你开始10个threads时，那么第一个获得semaphore的得到critical section的访问权。
         * 剩下的线程都会被semaphore阻塞直到那个获得semaphore的线程释放它。当这情况发生，semaphore在等待的线程中
         * 选择一个并给予它访问critical section的访问权。全部的任务都会打印文档，只是一个接一个的执行。
         */
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + " : Start printing...");
                    printQueue.printJob();
                    System.out.println(Thread.currentThread().getName() + " : Finished printing...");
                }
            }, "Thread-" + i).start();
        }

        TimeUnit.SECONDS.sleep(10);
    }

    /**
     * 3.3 控制并发访问多个资源
     * 有一个print queue但可以在3个不同的打印机上打印文件。
     */
    @Test
    public void testMultiSemaphores() throws InterruptedException {
        final PrintQueueWithMultiPrinters printQueueWithMultiPrinters = new PrintQueueWithMultiPrinters();
        /**
         */
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + " : Start printing...");
                    printQueueWithMultiPrinters.printJob();
                    System.out.println(Thread.currentThread().getName() + " : Finished printing...");
                }
            }, "Thread-" + i).start();
        }

        TimeUnit.SECONDS.sleep(10);
    }

    /**
     * 3.4 等待多个并发事件完成
     * 演示如何使用 CountDownLatch 类来实现 video- conference 系统。 video-conference 系统将等待全部参与者到达后才会开始。
     */
    @Test
    public void testCountDownLatch() throws InterruptedException {
        VideoConference conference = new VideoConference(10);
        new Thread(conference).start();
        for (int i = 0; i < 10; i++) {
            new Thread(new Participant("Participant-" + i, conference)).start();
        }
        TimeUnit.SECONDS.sleep(10);
    }

    /**
     * 3.5 在一个相同点同步任务
     * 使用 CyclicBarrier 类来让一组线程在一个确定点同步。并使用 Runnable 对象，在全部线程都到达确定点后被执行。
     * 在这个例子里，你将在数字矩阵中查找一个数字。矩阵会被分成多个子集（使用divide 和 conquer 技术），所以每个线
     * 程会在一个子集中查找那个数字。一旦全部行程运行结束，会有一个最终任务来统一他们的结果。
     */
    @Test
    public void testCyclicBarrier() throws InterruptedException {
        final int ROWS = 10000;
        final int NUMBERS = 1000;
        final int SEARCH = 5;
        final int PARTICIPANTS = 5;
        final int LINES_PARTICIPANT = 2000;

        MatrixMock mock = new MatrixMock(ROWS, NUMBERS, SEARCH);

        Results results = new Results(ROWS);

        Grouper grouper = new Grouper(results);

        // CyclicBarrier会等待PARTICIPANTS个线程执行结束，然后执行第二个参数给定的Runnable
        CyclicBarrier barrier = new CyclicBarrier(PARTICIPANTS, grouper);

        Searcher searchers[] = new Searcher[PARTICIPANTS];
        for (int i = 0; i < PARTICIPANTS; i++) {
            searchers[i] = new Searcher(LINES_PARTICIPANT * i, LINES_PARTICIPANT * (i + 1) - 1, mock, results, SEARCH, barrier);
            new Thread(searchers[i]).start();
        }
        TimeUnit.SECONDS.sleep(2);
    }

    /**
     * 3.6 运行并发阶段性任务
     * 使用Phaser类来同步3个并发任务。这3个任务会在3个不同的文件夹和它们的子文件夹中搜索扩展名是.log并在24小时内修改过的文件。这个任务被分成3个步骤：
     * 1. 在指定的文件夹和子文件夹中获得文件扩展名为.log的文件列表。
     * 2. 过滤第一步的列表中修改超过24小时的文件。
     * 3. 在操控台打印结果。
     * 在步骤1和步骤2的结尾我们要检查列表是否为空。如果为空，那么线程直接结束运行并从phaser类中淘汰。
     */
    @Test
    public void testPhaser() throws InterruptedException {
        // 创建 含3个参与者的 Phaser 对象。
        Phaser phaser = new Phaser(3);

        // 创建3个FileSearch对象，每个在不同的初始文件夹里搜索.log扩展名的文件。
        FileSearch spring = new FileSearch("/home/kang/Workspace/git/spring-projects/spring-framework", "log", phaser);
        FileSearch varlog = new FileSearch("/var/log", "log", phaser);
        FileSearch varlib = new FileSearch("var/lib", "log", phaser);

        Thread t1 = new Thread(spring, "spring");
        t1.start();
        Thread t2 = new Thread(varlog, "varlog");
        t2.start();
        Thread t3 = new Thread(varlib, "varlib");
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("Finished.");
    }

    /**
     * 3.7 控制并发阶段性任务的改变
     * 演示如何控制phaser的 phase的改变，通过实现自定义版本的 Phaser类并覆盖 onAdvance() 方法来执行一些每个phase 都会改变的行动。
     */
    @Test
    public void testExtendedPhaser() {
        // 创建 MyPhaser对象。
        MyPhaser phaser = new MyPhaser();

        // 创建5个 Student 对象并使用register()方法在phaser中注册他们。
        MyPhaser.Student students[] = new MyPhaser.Student[5];
        for (int i = 0; i < students.length; i++) {
            students[i] = phaser.new Student(phaser);
            phaser.register();
        }

        // 创建5个线程来运行students并开始它们。
        Thread threads[] = new Thread[students.length];
        for (int i = 0; i < students.length; i++) {
            threads[i] = new Thread(students[i], "Student " + i);
            threads[i].start();
        }

        // 等待5个线程的终结。
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 调用isTerminated()方法来写一条信息表明phaser是在termination状态。
        System.out.printf("Main: The phaser has finished: %s.\n",
                phaser.isTerminated());
    }

    /**
     * 3.8 在并发任务间交换数据
     * 演示如何使用 Exchanger 类来解决只有一个生产者和一个消费者的生产者和消费者问题。
     * 生产者进程产生数据，并通过Exchanger交换给消费者进程使用。
     */
    @Test
    public void testExchanger() throws InterruptedException {
        // 创建2个buffers。分别给producer和consumer使用.
        List<String> buffer1 = new ArrayList<>();
        List<String> buffer2 = new ArrayList<>();

        // 创建Exchanger对象，用来同步producer和consumer。
        Exchanger<List<String>> exchanger = new Exchanger<>();

        // 创建Producer对象和Consumer对象。
        Producer producer = new Producer(buffer1, exchanger);
        Consumer consumer = new Consumer(buffer2, exchanger);

        Thread tp = new Thread(producer);
        tp.start();
        Thread tc = new Thread(consumer);
        tc.start();

        tp.join();
        tc.join();


    }
}
