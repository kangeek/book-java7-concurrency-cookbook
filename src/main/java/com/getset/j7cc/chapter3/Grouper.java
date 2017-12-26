package com.getset.j7cc.chapter3;

/**
 * 计算数字在这个矩阵里出现的总数。它使用储存了矩阵中每行里数字出现次数的 Results 对象来进行运算。
 */
public class Grouper implements Runnable {
    private Results results;

    public Grouper(Results results) {
        this.results = results;
    }

    @Override
    public void run() {
        int finalResult = 0;
        System.out.println("Grouper: Processing results...");
        int data[] = results.getData();
        for (int number : data) {
            finalResult += number;
        }
        System.out.println("Grouper: Total result: " + finalResult);
    }
}
