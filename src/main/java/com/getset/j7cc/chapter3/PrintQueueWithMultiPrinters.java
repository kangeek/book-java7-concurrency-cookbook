package com.getset.j7cc.chapter3;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrintQueueWithMultiPrinters {

    private final Semaphore semaphore;
    private boolean[] freePrinters;
    private Lock lock;

    public PrintQueueWithMultiPrinters() {
        /**
         * Semaphore对象创建的构造方法是使用3作为参数的。
         * 这个例子中，前3个调用acquire() 方法的线程会获得临界区的访问权，其余的都会被阻塞 。
         * 当一个线程结束临界区的访问并解放semaphore时，另外的线程才可能获得访问权。
         */
        this.semaphore = new Semaphore(3);
        freePrinters = new boolean[3];
        for (int i = 0; i < 3; i++) {
            freePrinters[i] = true;
        }
        lock = new ReentrantLock();
    }

    public void printJob() {
        try {
            // 用acquire()方法获得demaphore。
            semaphore.acquire();
            int printerNo = getPrinter();
            System.out.println(Thread.currentThread().getName() + ": PrintQueue: Printing a Job using printer-" + printerNo + " during 3 seconds");
            Thread.sleep(3000);
            freePrinters[printerNo] = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 最后，释放semaphore通过调用semaphore的relaser()方法。
            semaphore.release();
        }
    }

    public int getPrinter() {
        try {
            lock.lock();
            for (int i = 0; i < 3; i++) {
                if (freePrinters[i]) {
                    freePrinters[i] = false;
                    return i;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return -1;
    }

}
