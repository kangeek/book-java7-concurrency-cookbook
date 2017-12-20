package com.getset.j7cc.chapter1;

public class MyThreadGroup extends ThreadGroup {
    public MyThreadGroup(String name) {
        super(name);
    }

    /**
     * 覆盖 uncaughtException() 方法。ThreadGroup 类的其中一个线程抛出异常时，就会调用此方法 。
     * 在这里，这个方法会把异常和抛出它的线程的信息写入操控台并中断ThreadGroup类的其余线程。
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.printf("The thread %s has thrown an Exception\n",t.getId());
        e.printStackTrace(System.out);
        System.out.printf("Terminating the rest of the Threads\n");
        interrupt();
    }
}
