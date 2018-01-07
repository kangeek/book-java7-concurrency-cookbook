package com.getset.j7cc.chapter6;

import java.util.concurrent.ConcurrentSkipListMap;

public class SkipListMapTask implements Runnable {
    private ConcurrentSkipListMap<Integer, String> map;
    private int threadId;

    public SkipListMapTask(int threadId, ConcurrentSkipListMap<Integer, String> map) {
        this.map = map;
        this.threadId = threadId;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            if (i % 2 == 0) {
                map.put(threadId * 1000 + i, "Thread-" + threadId + ":" + i);
            } else {
                map.put(threadId * 1000 + 1000 - i, "Thread-" + threadId + ":" + i);
            }
        }
    }
}
