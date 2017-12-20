package com.getset.j7cc.chapter1;

public class Calculator implements Runnable {
    private int number;

    public Calculator(int number) {
        this.number = number;
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.printf("%s: %d * %d = %d\n", Thread.currentThread().getName(), number, i, i * number);
        }
    }
}
