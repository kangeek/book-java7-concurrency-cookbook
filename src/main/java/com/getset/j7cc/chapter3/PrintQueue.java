package com.getset.j7cc.chapter3;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrintQueue {

    private final Semaphore semaphore;

    public PrintQueue() {
        // 传递值1作为此构造方法的参数，那么你就创建了一个binary semaphore。初始值为1，就保护了访问一个共享资源。
//        this.semaphore = new Semaphore(1);
        /**
         * fairness的内容是指全java语言的所有类中，那些可以阻塞多个线程并等待同步资源释放的类（例如，semaphore)。
         * 默认情况下是非公平模式。在这个模式中，当同步资源释放，就会从等待的线程中任意选择一个获得资源，但是这种选
         * 择没有任何标准。而公平模式可以改变这个行为并强制选择等待最久时间的线程。
         * 随着其他类的出现，Semaphore类的构造函数容许第二个参数。这个参数必需是 Boolean 值。如果你给的是 false，
         * 那么创建的semaphore就会在非公平模式下运行。如果你不使用这个参数，是跟给false值一样的结果。如果你给的是
         * true值，那么你创建的semaphore就会在公平模式下运行。
         */
        this.semaphore = new Semaphore(1, true);
    }

    public void printJob() {
        try {
            // 首先，你必须调用acquire()方法获得demaphore。
            semaphore.acquire();
            Long duration = (long) (Math.random() * 1000);
            System.out.println(Thread.currentThread().getName() + ": PrintQueue: Printing a Job during " + duration +
                    " milliseconds");
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 最后，释放semaphore通过调用semaphore的relaser()方法。
            semaphore.release();
        }
    }

}
