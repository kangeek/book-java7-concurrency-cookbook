package com.getset.j7cc.chapter1;

/**
 * 实现一个类抛出非检查异常
 */
public class DangrousTask implements Runnable {
    @Override
    public void run() {
        int numero = Integer.parseInt("ASDF");
    }
}
