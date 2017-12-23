package com.getset.j7cc.chapter2;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 在并发编程中发生的最常见的一种情况是超过一个执行线程使用共享资源。在并发应用程序中，多个线程读或写相同的数据或访问同
 * 一文件或数据库连接这是正常的。这些共享资源会引发错误或数据不一致的情况，我们必须通过一些机制来避免这些错误。
 * 解决这些问题从临界区的概念开始。临界区是访问一个共享资源在同一时间不能被超过一个线程执行的代码块。
 * <p>
 * Java(和 几乎所有的编程语言)提供同步机制，帮助程序员实现临界区。当一个线程想要访问一个临界区,它使用其中的一个同步机制
 * 来找出是否有任何其他线程执行临界区。如果没有，这个线程就进入临界区。否则，这个线程通过同步机制暂停直到另一个线程执行完
 * 临界区。当多个线程正在等待一个线程完成执行的一个临界 区，JVM选择其中一个线程执行，其余的线程会等待直到轮到它们。
 * <p>
 * Java语言提供的两种基本的同步机制:
 * 关键字synchronized
 * Lock接口及其实现
 */
public class BasicThreadSynchronization {

    /**
     * 2.2 线程的创建和运行
     * 只有一个线程能访问一个对象的声明为synchronized关键字的方法。如果一个线程A正在执行一个 synchronized方法，
     * 而线程B想要执行同个实例对象的synchronized方法，它将阻塞，直到线程A执行完。但是如果线程B访问相同类的不同实
     * 例对象，它们都不会被阻塞。
     */
    @Test
    public void testSyncMethod() throws InterruptedException {
        Account account = new Account();
        account.setBalance(100000);
        System.out.println("Account: Initial Blance is " + account.getBalance());

        Thread company = new Thread(new Company(account));
        Thread bank = new Thread(new Bank(account));

        company.start();
        bank.start();

        company.join();
        bank.join();

        System.out.println("Account: Final Blance is " + account.getBalance());
    }

    /**
     * 2.3 在同步的类里安排独立属性
     * <p>
     * 模拟一家电影院有两个屏幕和两个售票处。当一个售票处出售门票，它们用于两个电影院的其中一个，但不能用于两个，
     * 所以在每个电影院的免费席位的数量是独立的属性。
     * <p>
     * 使用专门的Object对象作为synchronized参数。
     */
    @Test
    public void testSyncParam() {
        Cinema cinema = new Cinema();
        TicketOffice1 ticketOffice1 = new TicketOffice1(cinema);
        Thread thread1 = new Thread(ticketOffice1);
        TicketOffice2 ticketOffice2 = new TicketOffice2(cinema);
        Thread thread2 = new Thread(ticketOffice2);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Room1 Vacancies: " + cinema.getVacanciesCinema1());
        System.out.println("Room2 Vacancies: " + cinema.getVacanciesCinema2());
    }

    /**
     * 2.4 在同步代码中使用条件
     * 通过使用synchronized关键字和wait()和notify(),notifyAll()方法实现生产者消费者问题。
     */
    @Test
    public void testWaitAndNotify() throws InterruptedException {
        EventStorage storage = new EventStorage();
        Thread producer = new Thread(new EventProducer(storage));
        Thread consumer = new Thread(new EventComsumer(storage));

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();
    }

    /**
     * 2.5 使用Lock来同步代码块
     * 通过锁来同步代码块和通过Lock接口及其实现者ReentrantLock类来创建临界区，实现一个程序来模拟打印队列。
     */
    @Test
    public void testLockSync() throws InterruptedException {
        final PrintQueue printQueue = new PrintQueue();

        class Job implements Runnable {
            @Override
            public void run() {
                System.out.printf("%s: Going to print a document\n", Thread.
                        currentThread().getName());
                // 打印方法中有Lock，因此只能依次使用
                printQueue.printJob(new Object());
                System.out.printf("%s: The document has been printed\n",
                        Thread.currentThread().getName());
            }
        }

        Job job = new Job();

        Thread threads[] = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(job, "Thread-" + i);
            threads[i].start();
        }

        TimeUnit.SECONDS.sleep(100);
    }

    /**
     * 2.6 使用读/写锁来同步数据访问
     * 使用ReadWriteLock接口实现一个程序，使用它来控制访问一个存储两个产品价格的对象。
     */
    @Test
    public void testReadWriteLock() throws InterruptedException {

        final PricesInfo pricesInfo = new PricesInfo();

        class Reader implements Runnable {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    System.out.printf("%s: Price 1: %f\n", Thread.
                            currentThread().getName(), pricesInfo.getPrice1());
                    System.out.printf("%s: Price 2: %f\n", Thread.
                            currentThread().getName(), pricesInfo.getPrice2());
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        class Writer implements Runnable {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    System.out.printf("Writer: Attempt to modify the prices.\n");
                    pricesInfo.setPrice1(Math.random() * 10);
                    pricesInfo.setPrice2(Math.random() * 8);
                    System.out.printf("Writer: Prices have been modified.\n");
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Thread writer = new Thread(new Writer());
        writer.start();

        for (int i = 0; i < 5; i++) {
            Thread t = new Thread(new Reader());
            t.start();
        }

        TimeUnit.SECONDS.sleep(5);
    }

    /**
     * 2.7 修改Lock的公平性
     * 修改使用Lock同步代码块食谱示例来使用这个属性，并且观察公平与非公平模式之间的差别。
     */
    @Test
    public void testLockSyncUsingFair() throws InterruptedException {
        final PrintQueue printQueue = new PrintQueue();

        class Job implements Runnable {
            @Override
            public void run() {
                System.out.printf("%s: Going to print a document\n", Thread.
                        currentThread().getName());
                // 打印方法中有Lock，因此只能依次使用
                printQueue.printJobUsingFair(new Object());
                System.out.printf("%s: The document has been printed\n",
                        Thread.currentThread().getName());
            }
        }

        Job job = new Job();

        /**
         * 所有线程都创建一个0.1秒的差异，第一需要获取锁的控制权的线程是Thread0，然后是Thread1，以此类推。
         * 当Thread0正在运行第一个由锁 保护的代码块时，有9个线程正在那个代码块上等待执行。当Thread0释放锁，
         * 它需要马上再次获取锁，所以我们有10个线程试图获取这个锁。当启用代码 模式，Lock接口将会选择Thread1，
         * 它是在这个锁上等待最长时间的线程。然后，选择Thread2，然后是Thread3，以此类推。直到所有线 程都
         * 通过了这个锁保护的第一个代码块，否则，没有一个线程能执行该锁保护的第二个代码块。
         * 一旦所有线程已经执行完由这个锁保护的第一个代码块，再次轮到Thread0。然后，轮到Thread1，以此类推。
         */
        Thread threads[] = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(job, "Thread-" + i);
            threads[i].start();
            Thread.sleep(100);
        }

        TimeUnit.SECONDS.sleep(200);
    }

    /**
     * 2.8 在Lock中使用多条件
     * 使用锁和条件来实现生产者与消费者问题。
     */
    @Test
    public void testCondition() {
        final FileMock fileMock = new FileMock(100, 10);
        final Buffer buffer = new Buffer(20);

        class Producer implements Runnable {
            @Override
            public void run() {
                buffer.setPendingLines(true);
                while (fileMock.hasMoreLines()) {
                    String line = fileMock.getLine();
                    buffer.insert(line);
                }
                buffer.setPendingLines(false);
            }
        }

        class Consumer implements Runnable {

            @Override
            public void run() {
                while (buffer.hasPendingLines()) {
                    buffer.get();
                    try {
                        Random random = new Random();
                        Thread.sleep(random.nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        new Thread(new Producer()).start();
        for (int i = 0; i < 3; i++) {
            new Thread(new Consumer()).start();
        }

        try {
            TimeUnit.SECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
