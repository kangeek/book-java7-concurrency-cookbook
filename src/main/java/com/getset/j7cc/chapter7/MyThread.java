package com.getset.j7cc.chapter7;

import java.util.Date;

public class MyThread extends Thread {
    private Date creationDate;
    private Date startDate;
    private Date endDate;

    public MyThread(Runnable r, String name) {
        super(r, name);
        this.creationDate = new Date();
    }

    @Override
    public void run() {
        startDate = new Date();
        super.run();
        endDate = new Date();
    }

    @Override
    public String toString() {
        return "MyThread[" + this.getName() + "] created on " + creationDate
            + ", started on " + startDate + ", ended on " + endDate;
    }
}
