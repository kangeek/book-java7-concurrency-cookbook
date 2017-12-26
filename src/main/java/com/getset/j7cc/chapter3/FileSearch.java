package com.getset.j7cc.chapter3;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * 在文件夹和其子文件夹中搜索确定的扩展名并在24小时内修改的文件。
 *   1. 在指定的文件夹和子文件夹中获得文件扩展名为.log的文件列表。
 *   2. 过滤第一步的列表中修改超过24小时的文件。
 *   3. 在操控台打印结果。
 * 在步骤1和步骤2的结尾我们要检查列表是否为空。如果为空，那么线程直接结束运行并从phaser类中淘汰。
 */
public class FileSearch implements Runnable {

    private String initPath;
    private String end;
    private List<String> results;
    private Phaser phaser;

    public FileSearch(String initPath, String end, Phaser phaser) {
        this.initPath = initPath;
        this.end = end;
        this.results = new ArrayList<>();
        this.phaser = phaser;
    }

    /**
     * 接收File对象作为参数并处理全部的文件和子文件夹。对于每个文件夹，此方法会递归调用并传递文件夹作为参数。对于每个文件，此方法会调用fileProcess()方法。
     */
    private void directoryProcess(File directory) {
        File list[] = directory.listFiles();
        if (list != null) {
            for (File file : list) {
                if (file.isDirectory()) {
                    directoryProcess(file);
                } else {
                    fileProcess(file);
                }
            }
        }
    }

    /**
     * 检查file的扩展名是否是我们正在查找的。如果是，此方法会把文件的绝对路径写入结果列表内。
     */
    private void fileProcess(File file) {
        if (file.getName().endsWith(end)) {
            results.add(file.getAbsolutePath());
        }
    }

    /**
     * 过滤出1天内修改的文件
     */
    private void filterResults() {
        List<String> newResults = new ArrayList<>();
        long actualDate = new Date().getTime();
        for (String filepath : results) {
            File file = new File(filepath);
            long fileDate = file.lastModified();

            if (actualDate - fileDate < TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)) {
                newResults.add(filepath);
            }
        }

        results = newResults;
    }

    /**
     * 此方法在第一个和第二个phase的结尾被调用，并检查结果是否为空。此方法不接收任何参数。
     */
    private boolean checkResults() {
        if (results.isEmpty()) {
            System.out.println(Thread.currentThread().getName() + ": Phase " + phaser.getPhase() + " : 0 results.");
            /**
             * 使用arriveAndDeregister() 通知phaser，该线程结束了actual phase， 表示该线程将不会继续参与后面的phases,
             * 所以请phaser不要再等待它了。
             */
            phaser.arriveAndDeregister();
            return false;
        } else {
            System.out.println(Thread.currentThread().getName() + ": Phase " + phaser.getPhase() + " : " + results.size() + " results.");
            phaser.arriveAndAwaitAdvance();
            return true;
        }
    }

    /**
     * 打印results list 的元素到控制台。
     */
    private void showInfo() {
        for (String filepath : results) {
            File file = new File(filepath);
            System.out.println(Thread.currentThread().getName() + " : " + file.getAbsolutePath());
        }
        // 保证所有线程一同结束
        phaser.arriveAndAwaitAdvance();
    }

    /**
     * 使用之前描述的辅助方法来执行，并使用Phaser对象控制phases间的改变。首先，调用phaser对象的
     * arriveAndAwaitAdvance() 方法。直到使用线程被创建完成，搜索行为才会开始。
     */
    @Override
    public void run() {
        /**
         * 调用Phaser对象的 arriveAndAwaitAdvance() 方法，Phaser知道我们要同步的线程的数量。当某个线程调用此方法，
         * Phaser减少终结actual phase的线程数，并让这个线程进入休眠 直到全部其余线程结束phase。
         * run方法一开始就调用，是为了等待所有的线程都创建完成后再一同开始。
         */
        phaser.arriveAndAwaitAdvance();
        System.out.println(Thread.currentThread().getName() + ": Starting.");
        File file = new File(initPath);
        if (file.isDirectory()) {
            directoryProcess(file);
        }
        if (!checkResults()) {
            return;
        }
        filterResults();

        if (!checkResults()) {
            return;
        }

        showInfo();
        phaser.arriveAndDeregister();
        System.out.println(Thread.currentThread().getName() + ": Work completed.");
    }
}
