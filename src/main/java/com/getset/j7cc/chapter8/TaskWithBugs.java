package com.getset.j7cc.chapter8;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

// 1. 创建一个类，名为 TaskWithBugs，扩展 Runnable 接口.
public class TaskWithBugs implements Runnable {
    // 2. 声明一个 ReentrantLock 属性，名为 Lock.private

    ReentrantLock lock;// 3. 实现类的构造函数。public

    TaskWithBugs(ReentrantLock lock) {
        this.lock = lock;
    }

    // 4. 实现run() 方法。获取lock的控制，让线程休眠2秒再释放lock。
    @Override
    public void run() {
        lock.lock();
        try {
            TimeUnit.SECONDS.sleep(1);
            lock.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 5.创建例子的主类通过创建一个类，名为 Main 并添加 main()方法。
    public static class Main {

        public static void main(String[] args) {
            // 6. 声明并创建一个 ReentrantLock 对象，名为 lock.
            ReentrantLock lock = new ReentrantLock();
            // 7. 创建10个 TaskWithBugs 对象和10个线程来执行这些任务。调用 run() 方法开始线程。
            for (int i = 0; i < 10; i++) {
                TaskWithBugs task = new TaskWithBugs(lock);
                Thread thread = new Thread(task);
                thread.run();
            }
        }

    }
}

