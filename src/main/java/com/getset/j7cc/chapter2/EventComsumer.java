package com.getset.j7cc.chapter2;

public class EventComsumer implements Runnable {

    private EventStorage storage;

    public EventComsumer(EventStorage storage) {
        this.storage = storage;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            storage.get();
        }
    }
}
