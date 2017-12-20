package com.getset.j7cc.chapter1;

import java.util.Date;
import java.util.Deque;

public class CleanerTask extends Thread {
    private Deque<Event> deque;

    public CleanerTask(Deque<Event> deque) {
        this.deque = deque;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            clean(new Date());
        }
    }

    private void clean(Date date) {
        long diff;
        boolean delete = false;
        if (deque.size() == 0) {
            return;
        }
        Event event = deque.getLast();
        diff = date.getTime() - event.getDate().getTime();
        while (diff > 10000) {
            System.out.println("Cleaner event " + event.getEvent());
            deque.removeLast();
            delete = true;
            event = deque.getLast();
            diff = date.getTime() - event.getDate().getTime();
        }
        if (delete) {
            System.out.println("After cleaning, size of queue: " + deque.size());
        }

    }
}
