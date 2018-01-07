package com.getset.j7cc.chapter7;

import java.util.concurrent.RecursiveTask;

public class MyRecursiveTask extends RecursiveTask<Integer> {
    private int[] array;
    private int start, end;

    public MyRecursiveTask(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        MyForkJoinWorkerThread thread = (MyForkJoinWorkerThread) Thread.currentThread();
        thread.addTask();
        if (start == end) {
            return array[start];
        }
        int middle = start + (end - start) / 2;
        MyRecursiveTask task1 = new MyRecursiveTask(array, start, middle);
        MyRecursiveTask task2 = new MyRecursiveTask(array, middle + 1, end);
        task1.fork();
        task2.fork();
        return task1.join() + task2.join();
    }

}
