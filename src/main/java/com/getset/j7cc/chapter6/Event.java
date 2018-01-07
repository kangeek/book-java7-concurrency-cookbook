package com.getset.j7cc.chapter6;

public class Event implements Comparable<Event> {
    private int thread;
    private int priority;

    public Event(int thread, int priority) {
        this.thread = thread;
        this.priority = priority;
    }

    public int getThread() {
        return thread;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(Event o) {
        if (this.priority > o.priority) {
            return 1;
        } else if (this.priority < o.priority) {
            return -1;
        } else {
            return 0;
        }
//        if (this.thread > o.thread) {
//            return 1;
//        } else if (this.thread < o.thread) {
//            return -1;
//        } else {
//            return 0;
//        }
    }
}
