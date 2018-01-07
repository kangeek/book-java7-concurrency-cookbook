package com.getset.j7cc.chapter8;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TaskForLogging implements Runnable {
    Logger logger = MyLogger.getLogger(this.getClass().getName());
//    Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void run() {
        logger.entering(Thread.currentThread().getName(), "run()");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.exiting(Thread.currentThread().getName(), "run()");
    }
}
