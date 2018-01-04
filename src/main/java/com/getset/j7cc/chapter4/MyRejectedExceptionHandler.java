package com.getset.j7cc.chapter4;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class MyRejectedExceptionHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        System.out.printf(">>> RejectedTaskController: The task %s has been rejected\n", r.toString());
        System.out.printf(">>> RejectedTaskController: %s\n", executor.toString());
        System.out.printf(">>> RejectedTaskController: Terminating:%s\n", executor.isTerminating());
        System.out.printf(">>> RejectedTaksController: Terminated:%s\n", executor.isTerminated());
    }
}
