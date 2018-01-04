package com.getset.j7cc.chapter4;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class MyFutureTask extends FutureTask<Integer> {
    private String name;

    public MyFutureTask(Callable<Integer> callable) {
        super(callable);
        this.name = ((CallableTask)callable).getName();
    }

    /**
     * 定义任务执行完毕或被取消后的操作。
     */
    @Override
    protected void done() {
        if (isCancelled()) {
            System.out.println("done(): " + name + " has been canceled.");
        } else {
            System.out.println("done(): " + name + " has finished.");
        }
    }
}
