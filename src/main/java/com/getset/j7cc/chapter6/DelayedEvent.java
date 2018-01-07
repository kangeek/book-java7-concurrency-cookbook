package com.getset.j7cc.chapter6;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayedEvent implements Delayed {
    private Date start;

    public DelayedEvent(Date start) {
        this.start = start;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(start.getTime() - new Date().getTime(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        long diff = this.getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        if (diff < 0) {
            return -1;
        } else if (diff > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
