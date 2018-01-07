package com.getset.j7cc.chapter8;

import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

//1.   创建一个类，名为 Task ，实现 Runnable 接口.
public class TaskForPhase implements Runnable {
    //2.   声明一个私有 int 属性，名为 time。
    private int time;

    //3.   声明私有 Phaser 属性，名为 phaser.
    private Phaser phaser;

    //4.   实现类的构造函数，初始其属性值。
    public TaskForPhase(int time, Phaser phaser) {
        this.time = time;
        this.phaser = phaser;
    }

    //5.   实现 run() 方法。首先，使用 arrive() 方法指示 phaser 属性任务开始执行了。
    @Override
    public void run() {

        phaser.arrive();

        //6.   写信息到操控台表明阶段一开始，把线程放入休眠几秒，使用time属性来表明，再写信息到操控台表明阶段一结束，并使用 phaser 属性的 arriveAndAwaitAdvance() 方法来与剩下的任务同步。
        System.out.printf("%s: Entering phase 1.\n", Thread.currentThread().getName());
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s: Finishing phase 1.\n", Thread.currentThread().getName());
        phaser.arriveAndAwaitAdvance();

        //7.	为第二和第三阶段重复第一阶段的行为。在第三阶段的末端使用 arriveAndDeregister()方法代替 arriveAndAwaitAdvance() 方法。
        System.out.printf("%s: Entering phase 2.\n", Thread.currentThread().getName());
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s: Finishing phase 2.\n", Thread.currentThread().getName());
        phaser.arriveAndAwaitAdvance();

        System.out.printf("%s: Entering phase 3.\n", Thread.currentThread().getName());
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s: Finishing phase 3.\n", Thread.currentThread().getName());

        phaser.arriveAndDeregister();

    }
}
