# [第二章：基本线程同步](docs/chapter2.md)

## 2.1 介绍 

在并发编程中发生的最常见的一种情况是超过一个执行线程使用共享资源。在并发应用程序中，多个线程读或写相同的数据或访问同一文件或数据库连接这是正常的。这些共享资源会引发错误或数据不一致的情况，我们必须通过一些机制来避免这些错误。

解决这些问题从临界区的概念开始。临界区是访问一个共享资源在同一时间不能被超过一个线程执行的代码块。

Java(和 几乎所有的编程语言)提供同步机制，帮助程序员实现临界区。当一个线程想要访问一个临界区,它使用其中的一个同步机制来找出是否有任何其他线程执行临界 区。如果没有，这个线程就进入临界区。否则，这个线程通过同步机制暂停直到另一个线程执行完临界区。当多个线程正在等待一个线程完成执行的一个临界 区，JVM选择其中一个线程执行，其余的线程会等待直到轮到它们。

本章展示了一些的指南，指导如何使用Java语言提供的两种基本的同步机制:
  * 关键字synchronized
  * Lock接口及其实现

## 2.2 同步方法 

本节演示在Java中如何使用一个最基本的同步方法，即使用 synchronized关键字来控制并发访问方法。只有一个执行线程将会访问一个对象中被synchronized关键字声明的方法。如果另一个线程试图访问同一个对象中任何被synchronized关键字声明的方法，它将被暂停，直到第一个线程结束方法的执行。

换句话说，每个方法声明为synchronized关键字是一个临界区，Java只允许一个对象执行其中的一个临界区。

静态方法有不同的行为。只有一个执行线程访问被synchronized关键字声明的静态方法，但另一个线程可以访问该类的一个对象中的其他非静态的方法。 你必须非常小心这一点，因为两个线程可以访问两个不同的同步方法，如果其中一个是静态的而另一个不是。如果这两种方法改变相同的数据,你将会有数据不一致 的错误。

[示例](../src/test/java/com/getset/j7cc/chapter2/BasicThreadSyncronization.java#L23)将实现一个有两个线程访问共同对象的示例。我们将有一个银行帐户和两个线程：其中一个线程将钱转移到帐户而另一个线程将从账户中扣款。在没有同步方法，我们可能得到不正确的结果。同步机制保证了账户的正确。

## 2.3 在同步的类里安排独立属性

当你使用synchronized关键字来保护代码块时，你必须通过一个对象的引用作为参数。通常，你将会使用this关键字来引用执行该方法的对象，但是你也可以使用其他对象引用。通常情况下，这些对象被创建只有这个目的。比如，你在一个类中有被多个线程共享的两个独立属性。你必须同步访问每个变量，如果有一个线程访问一个属性和另一个线程在同一时刻访问另一个属性，这是没有问题的。

[示例](../src/test/java/com/getset/j7cc/chapter2/BasicThreadSyncronization.java#L47)编程模拟一家电影院有两个屏幕和两个售票处。当一个售票处出售门票，它们用于两个电影院的其中一个，但不能用于两个，所以在每个电影院的免费席位的数量是独立的属性。

## 2.4 在同步代码中使用条件

在并发编程中的一个经典问题是生产者与消费者问题，我们有一个数据缓冲区，一个或多个数据的生产者在缓冲区存储数据，而一个或多个数据的消费者，把数据从缓冲区取出。

由于缓冲区是一个共享的数据结构，我们必须采用同步机制，比如synchronized关键字来控制对它的访问。但是我们有更多的限制因素，如果缓冲区是满的，生产者不能存储数据，如果缓冲区是空的，消费者不能取出数据。

对于这些类型的情况，Java在Object对象中提供wait()，notify()，和notifyAll() 方法的实现。一个线程可以在synchronized代码块中调用wait()方法。如果在synchronized代码块外部调用wait()方法，JVM会抛出IllegalMonitorStateException异常。当线程调用wait()方法，JVM让这个线程睡眠，并且释放控制 synchronized代码块的对象，这样，虽然它正在执行但允许其他线程执行由该对象保护的其他synchronized代码块。为了唤醒线程，你必 须在由相同对象保护的synchronized代码块中调用notify()或notifyAll()方法。

[示例](../src/test/java/com/getset/j7cc/chapter2/BasicThreadSyncronization.java#L77)通过使用synchronized关键字和wait()和notify(),notifyAll()方法实现生产者消费者问题。

## 2.5 使用Lock来同步代码块

Java提供另外的机制用来同步代码块。它比synchronized关键字更加强大、灵活。它是基于Lock接口和实现它的类（如ReentrantLock）。这种机制有如下优势：

它允许以一种更灵活的方式来构建synchronized块。使用synchronized关键字，你必须以结构化方式得到释放synchronized代码块的控制权。Lock接口允许你获得更复杂的结构来实现你的临界区。

Lock 接口比synchronized关键字提供更多额外的功能。新功能之一是实现的tryLock()方法。这种方法试图获取锁的控制权并且如果它不能获取该锁，是因为其他线程在使用这个锁，它将返回这个锁。使用synchronized关键字，当线程A试图执行synchronized代码块，如果线程B正在执行它，那么线程A将阻塞直到线程B执行完synchronized代码块。使用锁，你可以执行tryLock()方法，这个方法返回一个 Boolean值表示，是否有其他线程正在运行这个锁所保护的代码。
当有多个读者和一个写者时，Lock接口允许读写操作分离。

Lock接口比synchronized关键字提供更好的性能。

[示例](../src/test/java/com/getset/j7cc/chapter2/BasicThreadSyncronization.java#L94)通过锁来同步代码块和通过Lock接口及其实现者ReentrantLock类来创建临界区，实现一个程序来模拟打印队列。

## 2.6 使用读/写锁来同步数据访问

锁所提供的最重要的改进之一就是ReadWriteLock接口和唯一 一个实现它的ReentrantReadWriteLock类。这个类提供两把锁，一把用于读操作和一把用于写操作。同时可以有多个线程执行读操作，但只有一个线程可以执行写操作。当一个线程正在执行一个写操作，不可能有任何线程执行读操作。

[示例](../src/test/java/com/getset/j7cc/chapter2/BasicThreadSyncronization.java#L125)使用ReadWriteLock接口实现一个程序，使用它来控制访问一个存储两个产品价格的对象。

## 2.7 修改Lock的公平性

在ReentrantLock类和 ReentrantReadWriteLock类的构造器中，允许一个名为fair的boolean类型参数，它允许你来控制这些类的行为。默认值为 false，这将启用非公平模式。在这个模式中，当有多个线程正在等待一把锁（ReentrantLock或者 ReentrantReadWriteLock），这个锁必须选择它们中间的一个来获得进入临界区，选择任意一个是没有任何标准的。true值将开启公平 模式。在这个模式中，当有多个线程正在等待一把锁（ReentrantLock或者ReentrantReadWriteLock），这个锁必须选择它们 中间的一个来获得进入临界区，它将选择等待时间最长的线程。考虑到之前解释的行为只是使用lock()和unlock()方法。由于tryLock()方 法并不会使线程进入睡眠，即使Lock接口正在被使用，这个公平属性并不会影响它的功能。

[示例](../src/test/java/com/getset/j7cc/chapter2/BasicThreadSyncronization.java#L179)修改使用Lock同步代码块食谱示例来使用这个属性，并且观察公平与非公平模式之间的差别。

## 2.8 在Lock中使用多条件

一个锁可能伴随着多个条件。这些条件声明在Condition接口中。 这些条件的目的是允许线程拥有锁的控制并且检查条件是否为true，如果是false，那么线程将被阻塞，直到其他线程唤醒它们。Condition接口提供一种机制，阻塞一个线程和唤醒一个被阻塞的线程。

[示例](../src/test/java/com/getset/j7cc/chapter2/BasicThreadSyncronization.java#L219)演示了使用锁和条件来实现生产者与消费者问题。

Condition接口提供不同版本的await()方法，如下：

  * await(long time, TimeUnit unit):这个线程将会一直睡眠直到
    * 它被中断
    * 其他线程在这个condition上调用singal()或signalAll()方法
    * 指定的时间已经过了
  * awaitUntil(Date date):这个线程将会一直睡眠直到
    * 它被中断
    * 其他线程在这个condition上调用singal()或signalAll()方法
    * 指定的日期已经到了
  * awaitUninterruptibly():这个线程将不会被中断，一直睡眠直到其他线程调用signal()或signalAll()方法

你可以在一个读/写锁中的ReadLock和WriteLock上使用conditions。