package com.getset.j7cc.chapter7;

public class MyEvent implements Comparable<MyEvent> {
    private String thread;
    private int priority;

    public MyEvent(String thread, int priority) {
        this.thread = thread;
        this.priority = priority;
    }

    public String getThread() {
        return thread;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(MyEvent o) {
        return Integer.compare(priority, o.priority);
    }
}
