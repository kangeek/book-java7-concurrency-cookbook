package com.getset.j7cc.chapter2;

public class EventProducer implements Runnable {

    private EventStorage storage;

    public EventProducer(EventStorage storage) {
        this.storage = storage;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            storage.set();
        }
    }
}
