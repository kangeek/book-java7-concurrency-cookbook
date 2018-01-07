package com.getset.j7cc.chapter7;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class MyPriorityTransferQueue<E> extends PriorityBlockingQueue<E> implements TransferQueue<E> {
    /**
     * 此属性储存了正在等待从数据类型提取元素的消费者的数量。当一个消费者调用 take()操作来从数据类型中提取元素时，
     * counter 数增加。当消费者结束 take() 操作的执行时，counter 数再次增加。在 hasWaitingConsumer() 和
     * getWaitingConsumerCount() 方法的实现中使用到了 counter。
     */
    private AtomicInteger counter;
    // 用来储存传输的元素
    private LinkedBlockingQueue<E> transfered;
    private ReentrantLock lock;

    public MyPriorityTransferQueue() {
        this.counter = new AtomicInteger(0);
        transfered = new LinkedBlockingQueue<>();
        lock = new ReentrantLock();
    }

    /**
     * 实现 tryTransfer() 方法。此方法尝试立刻发送元素给正在等待的消费者（如果可能）。如果没有任何消费者在等待，
     * 此方法返回 false 值。
     */
    @Override
    public boolean tryTransfer(E e) {
        boolean result;
        lock.lock();
        if (counter.get() == 0) {
            result = false;
        } else {
            put(e);
            result = true;
        }
        lock.unlock();
        return result;
    }

    /**
     * 实现 transfer() 方法。此方法尝试立刻发送元素给正在等待的消费者（如果可能）。如果没有任何消费者在等待，
     * 此方法把元素存入一个特殊queue，会阻塞线程直到元素被消耗。
     */
    @Override
    public void transfer(E e) throws InterruptedException {
        lock.lock();
        if (counter.get() != 0) {
            put(e);
            lock.unlock();
        } else {
            transfered.put(e);
            lock.unlock();
            synchronized (e) {
                e.wait();
            }
        }
    }

    /**
     * 实现 tryTransfer() 方法，它接收3个参数： 元素，和需要等待消费者的时间（如果没有消费者的话），和用来注明时间
     * 的单位。如果有消费者在等待，立刻发送元素。否则，转化时间到毫秒并使用 wait() 方法让线程进入休眠。当消费者取走
     * 元素时，如果线程在 wait() 方法里休眠，你将使用 notify() 方法唤醒它。
     */
    @Override
    public boolean tryTransfer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        lock.lock();
        if (counter.get() != 0) {
            put(e);
            lock.unlock();
            return true;
        } else {
            transfered.add(e);
            long milliTimeout = TimeUnit.MILLISECONDS.convert(timeout, unit);
            lock.unlock();
            e.wait(milliTimeout);
            lock.lock();
            if (transfered.contains(e)) {
                transfered.remove(e);
                lock.unlock();
                return false;
            } else {
                lock.unlock();
                return true;
            }
        }
    }

    @Override
    public boolean hasWaitingConsumer() {
        return counter.get() != 0;
    }

    @Override
    public int getWaitingConsumerCount() {
        return counter.get();
    }

    /**
     * 此方法是当消费者需要元素时被消费者调用的。首先，获取之前定义的锁并增加在等待的消费者数量。
     */
    @Override
    public E take() throws InterruptedException {
        lock.lock();
        counter.incrementAndGet();
        E value = transfered.poll();
        if (value == null) {
            lock.unlock();
            value = super.take();
            lock.lock();
        } else {
            synchronized (value) {
                value.notify();
            }
        }
        counter.decrementAndGet();
        lock.unlock();
        return value;
    }

}
