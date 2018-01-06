package com.getset.j7cc.chapter5;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;

public class DocumentSearchTask extends RecursiveTask<Integer> {
    private String[][] document;
    private int start;
    private int end;
    private String wordToSearch;
    private boolean isAsynchronized;

    DocumentSearchTask(String[][] document, int start, int end, String wordToSearch, boolean isAsynchronized) {
        this.document = document;
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
            DocumentSearchTask t1 = new DocumentSearchTask(document, start, middle, wordToSearch, isAsynchronized);
            DocumentSearchTask t2 = new DocumentSearchTask(document, middle, end, wordToSearch, isAsynchronized);
            /**
             * 以下内容用于“5.3 加入任务的结果”：testForkJoinResult测试方法
             * 使用invokeAll进行同步调用
             */
            if (isAsynchronized) {
                invokeAll(t1, t2);
                try {
                    count = (t1.get() + t2.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            /**
             * 以下内容用于“5.4 异步运行任务”： testForkJoinResult测试方法
             * 使用fork()进行异步执行，使用join()获取结果
             */
            else {
                // 使用fork方法把自己提交给线程池
                t1.fork();
                t2.fork();
                count = t1.join() + t2.join();
            }

        } else {
            List<LineSearchTask> lineSearchTasks = new ArrayList<>();
            if (isAsynchronized) {
                for (int i = start; i < end; i++) {
                    lineSearchTasks.add(new LineSearchTask(document[i], 0, document[i].length, wordToSearch, isAsynchronized));
                }
                invokeAll(lineSearchTasks);
                for (LineSearchTask task :
                        lineSearchTasks) {
                    try {
                        count += task.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                for (int i = start; i < end; i++) {
                    LineSearchTask task = new LineSearchTask(document[i], 0, document[i].length, wordToSearch, isAsynchronized);
                    task.fork();
                    count += task.join();
                }
            }
        }
        return count;
    }
}
