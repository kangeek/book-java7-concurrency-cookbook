package com.getset.j7cc.chapter6;

import org.junit.Test;

import java.util.Date;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class ConcurrentCollections {
    /**
     * 6.2 使用非阻塞线程安全列表
     * 非阻塞列表提供这些操作：如果操作不能立即完成（比如，你想要获取列表的元素而列表却是空的），它将根据这个操作抛出异常或返回null值。Java 7引进实现了非阻塞并发列表的ConcurrentLinkedDeque类。
     * 我们将使用以下两种不同任务来实现一个例子：
     * 1 大量添加数据到列表
     * 2 在同个列表中，大量删除数据
     */
    @Test
    public void testNonBlockingList() throws InterruptedException {
        /**
         * 使用ConcurrentLinkedDeque可以正确保存和取出Deque中的值；
         * 而使用LinkedList这种线程不安全的Deque无法保证元素的存取。
         */
        Deque<String> deque = new ConcurrentLinkedDeque<>();
//        Deque<String> deque = new LinkedList<>();
        Thread[] threads = new Thread[100];
        for (int i = 0; i < threads.length; i++) {
            AddTask task = new AddTask(deque);
            threads[i] = new Thread(task);
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        System.out.println("Deque size: " + deque.size());

        for (int i = 0; i < 100; i++) {
            PollTask task = new PollTask(deque);
            threads[i] = new Thread(task);
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        System.out.println("Deque size: " + deque.size());
    }

    /**
     * 6.3 使用阻塞线程安全列表
     * 阻塞列表与非阻塞列表的主要区别是，阻塞列表有添加和删除元素的方法，如果由于列表已满或为空而导致这些操作
     * 不能立即进行，它们将阻塞调用的线程，直到这些操作可以进行。Java包含实现阻塞列表的LinkedBlockingDeque类。
     */
    @Test
    public void testLinkedBlockingDeque() throws InterruptedException {
        final LinkedBlockingDeque<Integer> deque = new LinkedBlockingDeque<>(3);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 20; i++) {
                    /**
                     * add(): 如果队列满了，会抛出异常 IllegalStateException: Deque full
                     * put(): 如果队列满了，对阻塞，等队列有空余为止再添加
                     */
//                        deque.add(i);
                    try {
                        deque.put(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("[" + deque.size() + "]" + "Add: " + i + " at " + new Date());
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        for (int i = 0; i < 20; i++) {
            /**
             * poll(): 如果没有元素，返回null
             * getFirst()、getLast(): 如果没有元素，抛出NoSuchElementException异常
             * take(): 如果没有元素，会阻塞等待
             */
//            System.out.println("[" + deque.size() + "]" + "Get: " + deque.poll() + " at           " + new Date());
//            System.out.println("[" + deque.size() + "]" + "Get: " + deque.getFirst() + " at           " + new Date());
            System.out.println("[" + deque.size() + "]" + "Get: " + deque.take() + " at            " + new Date());
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * 6.4 用优先级对使用阻塞线程安全列表排序
     * PriorityBlockingQueue能够实现线程安全的排序列表，只需要元素实现Comparable接口即可。
     */
    @Test
    public void testPriorityBlockingQueue() throws InterruptedException {
        PriorityBlockingQueue<Event> queue = new PriorityBlockingQueue<>();
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(new EventTask(i, queue));
            threads[i].start();
        }
        for (int i = 0; i < 5; i++) {
            threads[i].join();
        }
        System.out.println("Queue size: " + queue.size());
        for (int i = 0; i < 50000; i++) {
            System.out.print(" " + queue.take().getPriority());
//            System.out.print(" " + queue.take().getThread());
        }
    }

    /**
     * 6.5 使用线程安全与带有延迟元素的列表
     * 使用DelayedQueue类来存储一些具有不同激活日期的事件。
     */
    @Test
    public void testDelayQueue() throws InterruptedException {
        DelayQueue<DelayedEvent> events = new DelayQueue<>();
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(new DelayedEventTask(i, events));
            threads[i].start();
        }
        for (int i = 0; i < 5; i++) {
            threads[i].join();
        }
        for (int i = 0; i < 5000; i++) {
            // 只有DelayedEvent到达构造方法指定的start的时间的时候才可以取出，否则阻塞
            events.take();
            System.out.println(events.size());
        }
    }

    /**
     * 6.6 使用线程安全的NavigableMap
     * ConcurrentSkipListMap是一种线程安全的非阻塞、自排序的键值对数据结构。
     */
    @Test
    public void testSkipListMap() throws InterruptedException {
        ConcurrentSkipListMap<Integer, String> map = new ConcurrentSkipListMap<>();
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(new SkipListMapTask(i, map));
            threads[i].start();
        }
        for (int i = 0; i < 5; i++) {
            threads[i].join();
        }

        /**
         * 取出Map中的内容，发现是以key值进行排序
         */
        while (!map.isEmpty()) {
            Map.Entry<Integer, String> entry = map.pollFirstEntry();
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    /**
     * 6.7 生成并行随机数
     * 使用ThreadLocalRandom生成线程独立的随机数。
     */
    @Test
    public void testThreadSafeRandom() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        System.out.println(ThreadLocalRandom.current().nextInt(100));
                    }
                }
            }).start();
        }
        TimeUnit.SECONDS.sleep(2);
    }

    /**
     * 6.8 使用原子变量
     * 使用AtomicInteger保证多线程的自增操作是原子的，从而线程安全。
     * 同时使用普通的Integer做对比。
     */
    @Test
    public void testAtomicData() throws InterruptedException {
        final Integer[] int1 = {0};
        final AtomicInteger int2 = new AtomicInteger(0);
        for (int i = 0; i < 20; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10000; j++) {
                        int1[0] = int1[0] + 1;
                        // 原子变量的操作能够保证原则性
                        int2.getAndIncrement();
                    }
                }
            }).start();
        }
        TimeUnit.SECONDS.sleep(2);
        System.out.println("int1: " + int1[0] + ", int2: " + int2.get());
    }

    /**
     * 6.9 使用原子阵列
     * 使用AtomicIntegerArray 类来操作原子 arrays。
     */
    @Test
    public void testAtomicArray() throws InterruptedException {
        final AtomicIntegerArray array = new AtomicIntegerArray(1000);
        Thread[] incThreads = new Thread[100];
        Thread[] decThreads = new Thread[100];
        for (int i = 0; i < 100; i++) {
            incThreads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 1000; j++) {
                        // 针对元素的原子操作
                        array.getAndIncrement(j);
                    }
                }
            });
            incThreads[i].start();

            decThreads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 1000; j++) {
                        // 针对元素的原子操作
                        array.getAndDecrement(j);
                    }
                }
            });
            decThreads[i].start();
        }

        for (int i = 0; i < 100; i++) {
            incThreads[i].join();
            decThreads[i].join();
        }

        for (int i = 0; i < 1000; i++) {
            System.out.print(" " + array.get(i));
        }
    }
}
