package com.getset.j7cc.chapter2;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PricesInfo {
    private double price1;
    private double price2;
    private ReentrantReadWriteLock lock;

    public PricesInfo() {
        price1 = 1.0;
        price2 = 2.0;
        lock = new ReentrantReadWriteLock();
    }

    public double getPrice1() {
        lock.readLock().lock();
        double value = price1;
        lock.readLock().unlock();
        return value;
    }

    public void setPrice1(double price1) {
        lock.writeLock().lock();
        this.price1 = price1;
        lock.writeLock().unlock();
    }

    public double getPrice2() {
        lock.readLock().lock();
        double value = price2;
        lock.readLock().unlock();
        return value;
    }

    public void setPrice2(double price2) {
        lock.writeLock().lock();
        this.price2 = price2;
        lock.writeLock().unlock();
    }

}
