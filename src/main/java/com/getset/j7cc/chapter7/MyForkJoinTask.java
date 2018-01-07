package com.getset.j7cc.chapter7;

import java.util.concurrent.ForkJoinTask;

public abstract class MyForkJoinTask extends ForkJoinTask<Void> {
    private String name;

    public MyForkJoinTask(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * 实现 getRawResult() 方法。这是 ForkJoinTask 类的抽象方法之一。由于任务不会返回任何结果，此方法返回的一定是null值。
     */
    @Override
    public Void getRawResult() {
        return null;
    }

    /**
     * 实现 setRawResult() 方法。这是 ForkJoinTask 类的另一个抽象方法。由于任务不会返回任何结果，方法留白即可。
     */
    @Override
    protected void setRawResult(Void value) {

    }

    /**
     * 实现 exec() 方法。这是任务的主要方法。在这个例子，把任务的算法委托给 compute() 方法。计算方法的运行时间并写入操控台。
     */
    @Override
    protected boolean exec() {
        long start = System.currentTimeMillis();
        compute();
        System.out.printf("MyWorkerTask: %s : %d Milliseconds to complete.\n",name,System.currentTimeMillis() - start);
        return true;
    }

    /**
     * 与 RecursiveAction 和 RecursiveTask 类似，将 compute 方法教给具体任务去实现。
     */
    protected abstract void compute();
}
