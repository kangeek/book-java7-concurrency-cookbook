package com.getset.j7cc.chapter8;

import java.util.concurrent.TimeUnit;

//1. 创建一个类，名为 Task，并实现 Runnable 接口.
public class TaskForExecutor implements Runnable {

    //2. 声明一个私有 long 属性，名为 milliseconds.
    private long milliseconds;

    //3. 实现类的构造函数，初始化它的属性。
    public TaskForExecutor(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    //4. 实现 run() 方法。通过 milliseconds 属性让线程进入一段时间休眠。
    @Override
    public void run() {
        System.out.printf("%s: Begin\n", Thread.currentThread().getName());
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s: End\n", Thread.currentThread().getName());
    }
}
