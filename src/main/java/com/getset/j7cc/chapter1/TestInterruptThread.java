package com.getset.j7cc.chapter1;

/**
 * 一个线程在未正常结束之前, 被强制终止是很危险的事情. 因为它可能带来完全预料不到的严重后果. 所以你看到Thread.suspend, Thread.stop等方法都被Deprecated了.
 *
 * 那么不能直接把一个线程搞挂掉, 但有时候又有必要让一个线程死掉, 或者让它结束某种等待的状态 该怎么办呢?
 *   优雅的方法就是, 给那个线程一个中断信号, 让它自己决定该怎么办.
 *     比如说, 在某个子线程中为了等待一些特定条件的到来, 你调用了Thread.sleep(10000), 预期线程睡10秒之后自己醒来, 但是如果这个特定条件提前到来的话, 你怎么通知一个在睡觉的线程呢?
 *     又比如说, 主线程通过调用子线程的join方法阻塞自己以等待子线程结束, 但是子线程运行过程中发现自己没办法在短时间内结束, 于是它需要想办法告诉主线程别等我了. 这些情况下, 就需要中断.
 *
 * 中断是通过调用Thread.interrupt()方法来做的. 这个方法通过修改了被调用线程的中断状态来告知那个线程, 说它被中断了.
 *   对于非阻塞中的线程, 只是改变了中断状态, 即Thread.isInterrupted()将返回true;
 *   对于可取消的阻塞状态中的线程, 比如等待在这些函数上的线程, Thread.sleep(), Object.wait(), Thread.join(),
 *     这个线程收到中断信号后, 会抛出InterruptedException, 同时会把中断状态置回为false.
 */
public class TestInterruptThread extends Thread {
    /**
     * 每秒钟检查一次中断状态
     */
    public void run(){
        while(true){
            /**
             * interrupted()是Thread的静态方法，他会在判断是否中断后将中断状态恢复回“未中断”，表示“已阅”。所以它只能对自己所在线程有效。
             * isInterrupted()方法只是检查被调用的线程是否中断。
             */
//            if(isInterrupted()){
            if(Thread.interrupted()){
                    System.out.println("Someone interrupted me.");
                }
                else{
                    System.out.println("Going...");
                }

            /**
             * sleep时如果收到传入的中断信号，会抛出InterruptedException异常。
             */
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                System.out.println("Someone interrupted me.");
//            }

            long now = System.currentTimeMillis();
            while(System.currentTimeMillis()-now<1000){
                // 为了避免Thread.sleep()而需要捕获InterruptedException而带来的理解上的困惑,
                // 可以用这种方法空转1秒
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TestInterruptThread t = new TestInterruptThread();
        t.start();
        Thread.sleep(3000);
        t.interrupt();
    }
}
