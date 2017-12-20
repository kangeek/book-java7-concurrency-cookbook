package com.getset.j7cc.chapter1;

public class PrimeGenerator extends Thread {
    @Override
    public void run() {
        long number = 1L;
        while (true) {
            if (isPrime(number)) {
                System.out.println("Number " + number + " is Prime");
            }
            /**
             * 线程中断仅仅是置线程的中断状态位，不会停止线程。需要用户自己去监视线程的状态为并做处理。
             * Thread 类还有其他可以检查线程是否被中断的方法。例如，静态方法interrupted()能检查当前线程是否被中断。
             * isInterrupted()和interrupted() 方法有着很重要的区别。
             * 第一个不会改变interrupted属性值，但是第二个会设置成false。
             * interrupted() 方法是一个静态方法，建议使用isInterrupted()方法。
             */
            if (isInterrupted()) {
                System.out.println("The Prime Generator has been interrupted.");
                return;
            }
            number++;
        }
    }

    private boolean isPrime(long number) {
        if (number <= 2) {
            return true;
        }
        for (long i = 2; i < number; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
}
