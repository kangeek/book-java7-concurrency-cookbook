package com.getset.j7cc.chapter3;

import java.util.Date;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

public class MyPhaser extends Phaser {
    @Override
    protected boolean onAdvance(int phase, int registeredParties) {
        switch (phase) {
            case 0:
                return studentsArrived();
            case 1:
                return finishFirstExercise();
            case 2:
                return finishSecondExercise();
            case 3:
                return finishExam();
            default:
                return true;
        }
    }

    // 辅助方法 studentsArrived()。它在操控台写2条信息，并返回false值来表明phaser将继续执行。
    private boolean studentsArrived() {
        System.out.println("-------------------------------------------------------------");
        System.out.printf("Phaser: The exam are going to start. The students are ready.\n");
        System.out.printf("Phaser: We have %d students.\n",
                getRegisteredParties());
        System.out.println("-------------------------------------------------------------");
        return false;
    }

    // 辅助方法 finishFirstExercise()。它在操控台写2条信息，并返回false值来表明phaser将继续执行。
    private boolean finishFirstExercise() {
        System.out.println("-------------------------------------------------------------");
        System.out.printf("Phaser: All the students have finished the first exercise.\n");
        System.out.printf("Phaser: It's time for the second one.\n");
        System.out.println("-------------------------------------------------------------");
        return false;
    }

    // 辅助方法 finishSecondExercise()。它在操控台写2条信息，并返回false值来表明phaser将继续执行。
    private boolean finishSecondExercise() {
        System.out.println("-------------------------------------------------------------");
        System.out.printf("Phaser: All the students have finished the second exercise.\n");
        System.out.printf("Phaser: It's time for the third one.\n");
        System.out.println("-------------------------------------------------------------");
        return false;
    }

    // 辅助方法 finishExam()。它在操控台写2条信息，并返回false值来表明phaser将继续执行。
    private boolean finishExam() {
        System.out.println("-------------------------------------------------------------");
        System.out.printf("Phaser: All the students have finished the exam.\n");
        System.out.printf("Phaser: Thank you for your time.\n");
        System.out.println("-------------------------------------------------------------");
        return true;
    }

    public class Student implements Runnable {

        private Phaser phaser;

        public Student(Phaser phaser) {
            this.phaser = phaser;
        }

        @Override
        public void run() {
            // 首先，写一条信息到操控台表明学生到达考场并调用 phaser 的 arriveAndAwaitAdvance()
            // 方法来等待其他线程们。
            System.out.printf("%s: Has arrived to do the exam. %s\n", Thread
                    .currentThread().getName(), new Date());
            phaser.arriveAndAwaitAdvance();

            // 然后，写信息到操控台，调用私有 doExercise1() 方法模拟第一场测验，写另一条信息到操控台并调用 phaser
            // 的 arriveAndAwaitAdvance() 方法来等待其他学生结束第一场测验。
            System.out.printf("%s: Is going to do the first exercise. %s\n",
                    Thread.currentThread().getName(), new Date());
            doExercise1();
            System.out.printf("%s: Has done the first exercise. %s\n", Thread
                    .currentThread().getName(), new Date());
            phaser.arriveAndAwaitAdvance();

            // 为第二场和第三场实现相同的代码。
            System.out.printf("%s: Is going to do the second exercise.%s\n",
                    Thread.currentThread().getName(), new Date());
            doExercise2();
            System.out.printf("%s: Has done the second exercise. %s\n", Thread
                    .currentThread().getName(), new Date());
            phaser.arriveAndAwaitAdvance();
            System.out.printf("%s: Is going to do the third exercise. %s\n",
                    Thread.currentThread().getName(), new Date());
            doExercise3();
            System.out.printf("%s: Has finished the exam. %s\n", Thread
                    .currentThread().getName(), new Date());
            phaser.arriveAndAwaitAdvance();
        }

        // 实现辅助方法 doExercise1()。此方法让线程随机休眠一段时间。
        private void doExercise1() {
            try {
                long duration = (long) (Math.random() * 10);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 实现辅助方法 doExercise2()。此方法让线程随机休眠一段时间。
        private void doExercise2() {
            try {
                long duration = (long) (Math.random() * 10);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 实现辅助方法 doExercise3()。此方法让线程随机休眠一段时间。
        private void doExercise3() {
            try {
                long duration = (long) (Math.random() * 10);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
