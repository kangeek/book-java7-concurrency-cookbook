package com.getset.j7cc.chapter5;

import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

public class LineSearchTask extends RecursiveTask<Integer> {
    private String[] line;
    private int start;
    private int end;
    private String wordToSearch;
    private boolean isAsynchronized;

    LineSearchTask(String[] line, int start, int end, String wordToSearch, boolean isAsynchronized) {
        this.line = line;
        this.start = start;
        this.end = end;
        this.wordToSearch = wordToSearch;
        this.isAsynchronized = isAsynchronized;
    }

    @Override
    protected Integer compute() {
        int count = 0;
        if (end - start > 10) {
            int middle = (start + end) / 2;
            LineSearchTask t1 = new LineSearchTask(line, start, middle, wordToSearch, isAsynchronized);
            LineSearchTask t2 = new LineSearchTask(line, middle, end, wordToSearch, isAsynchronized);
            /**
             * 使用invokeAll进行同步调用
             */
            if (isAsynchronized) {
                invokeAll(t1, t2);
                try {
                    count = (t1.get() + t2.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            /**
             * 使用fork()进行异步执行，使用join()获取结果
             */
            else {
                t1.fork();
                t2.fork();
                count = t1.join() + t2.join();
            }

        } else {
            for (int i = start; i < end; i++) {
                if (line[i].equals(wordToSearch)) {
                    count++;
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return count;
    }
}
