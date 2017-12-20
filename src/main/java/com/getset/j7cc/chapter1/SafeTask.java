package com.getset.j7cc.chapter1;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SafeTask implements Runnable {
    /**
     * 本地线程变量为每个使用这些变量的线程储存属性值。可以用 get() 方法读取值和使用 set() 方法改变值。
     * 第一次你访问本地线程变量的值，如果没有值给当前的线程对象，那么本地线程变量会调用 initialValue() 方法来设置值给线程并返回初始值。
     */
    private ThreadLocal<Integer> i = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return new Random().nextInt(100);
        }
    };

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " - number: " + i.get());
    }
}
