# 第八章: 测试并发应用程序

## 8.1 介绍

测试应用是很关键的任务。在应用准备好面向最终用户之前，你必须验证它的准确性。使用测试过程来证明应用的正确性已达成，并且错误已修改。

测试阶段是常见任务在任何软件开发和品质保证的过程。你可以找到很多关于测试过程和不同的角度切入的文学并应用到你的开发中。同时也有很多第三方库，例如：JUnit，和第三方应用，例如：Apache JMetter， 你可以用来自动化测试你的Java应用。在并发应用的开发中这是非常关键的。

由于并发应用有2个或多个线程共享数据结构和相互间的作用的情况，让测试阶段变的更加困难。当你测试并发应用时，你会遇到的最大的问题是执行非确定性的线程。你不能保证线程的执行顺序，使得错误很难重现。

在本章节，你将学到：

  * 如何获取并发应用的元素信息。这信息可以帮助你测试你的并发应用。
  * 如何使用 IDE (Integrated Development Environment) 和其他工作，例如：FindBugs 来测试并发应用。
  * 如何使用像MultithreadedTC之类的libraries来自动化测试。

## 8.2 监控锁接口

Lock 接口是Java 并发 API提供的最基本的机制来同步代码块。它允许定义临界区。临界区是代码块可以共享资源，但是不能被多个线程同时执行。此机制是通过Lock 接口和 ReentrantLock 类实现的。

[示例](../src/test/java/com/getset/j7cc/chapter8/ConcurrencyTesting.java#L14)学习从Lock对象可以获取的信息和如何获取这些信息。

在这个指南里，你实现的MyLock类扩展了ReentrantLock类来返回信息，除此之外获得不到这些信息 ，因为ReentrantLock 类里的数据都是保护类型的。 通过MyLock类实现的方法：

  * getOwnerName()：只有唯一一个线程可以执行被Lock对象保护的临界区。锁存储了正在执行临界区的线程。此线程会被ReentrantLock类的保护方法 getOwner()返回。 此方法使用 getOwner() 方法来返回线程的名字。
  * getThreads()：当线程正在执行临界区时，其他线程尝试进入临界区就会被放到休眠状态一直到他们可以继续执行为止。ReentrantLock类保护方法getQueuedThreads() 返回 正在等待执行临界区的线程list。此方法返回 getQueuedThreads() 方法返回的结果。

我们还使用了 ReentrantLock 类里实现的其他方法：

  * hasQueuedThreads():此方法返回 Boolean 值表明是否有线程在等待获取此锁
  * getQueueLength(): 此方法返回等待获取此锁的线程数量
  * isLocked(): 此方法返回 Boolean 值表明此锁是否为某个线程所拥有
  * isFair(): 此方法返回 Boolean 值表明锁的 fair 模式是否被激活

## 8.3 监控Phaser类

Java 并发 API 提供的其中一个最复杂且强大的功能是使用 Phaser 类来执行同步phased任务。当有些任务可以分成步骤执行时，此机制是很有用的。Phaser类提供的同步线程机制是在每个步骤的末端， 所以全部的线程都完成第一步后，才能开始执行第二步。

[示例](../src/test/java/com/getset/j7cc/chapter8/ConcurrencyTesting.java#L49)学习如何从Phaser类获取其状态信息。

我们使用以下的方法来获取phaser对象的状态：

  * getPhase():此方法返回phaser 任务的 actual phase
  * getRegisteredParties(): 此方法返回使用phaser对象作为同步机制的任务数
  * getArrivedParties(): 此方法返回已经到达actual phase末端的任务数
  * getUnarrivedParties(): 此方法返回还没到达actual phase末端的任务数

## 8.4 监控执行者框架

Executor 框架提供从线程的创建和管理来分别实现任务来执行这些任务的机制。如果你使用一个执行者，你只需要实现 Runnable 对象并把他们发送给执行者。 执行者的责任是管理线程。当你发一个任务给执行者，它会尝试使用pooled线程来执行这个任务，来避免创建新的任务。此机制由 Executor 接口提供，它是以 ThreadPoolExecutor 类来实现的。

[示例](../src/test/java/com/getset/j7cc/chapter8/ConcurrencyTesting.java#L77)学习从ThreadPoolExecutor执行者可以获取的信息和如何获取这些信息。

## 8.5 监控Fork/Join池

Executor 框架提供了线程的创建和管理，来实现任务的执行机制。Java 7 包括了一个 Executor 框架的延伸为了一种特别的问题而使用的，将比其他解决方案的表现有所提升(可以直接使用 Thread 对象或者 Executor 框架)。它是 Fork/Join 框架。

此框架是为了解决可以使用 divide 和 conquer 技术，使用 fork() 和 join() 操作把任务分成小块的问题而设计的。主要的类实现这个行为的是 ForkJoinPool 类。

[示例](../src/test/java/com/getset/j7cc/chapter8/ConcurrencyTesting.java#L122)学习从ForkJoinPool类可以获取的信息和如何获取这些信息。

## 8.6 编写有效的日志

log工具提供了允许你写信息到一个或者多个目的地的机制。一个Logger是由以下这些组成：

  * 一个或多个处理者: 一个处理者将决定目的地和log信息的格式。你可以把日志信息写入操控台，文档，或者数据库。
  * 名字: 通常Logger使用的名字是基于类名或者它的包名。
  * 等级: 日志信息有等级来表明它的重要性。Logger也有个等级是用来决定写什么样的信息。它只会写和这个等级一样重要的，或者更重要的信息。

为了以下2个主要目的，你应该使用log 系统：

  * 当异常被捕捉，写尽可能多的信息。这个会帮助你定位错误并解决它。
  * 写关于程序正在执行的类和方法的信息。

[示例](../src/test/java/com/getset/j7cc/chapter8/ConcurrencyTesting.java#L173)学习如何使用 java.util.logging 包提供的类来添加一个log系统到你的并发应用。

## 8.7 FindBugs分析并发代码

静态代码分析工具是一套通过分析应用源代码来查找潜在异常的工具。这些工具，例如 Checkstyle, PMD, 或者 FindBugs，他们有定义极好的实践（good practices） 规则，然后解析源代码来查找有没有违反这些规则。目的是在产品运行之前，更早的找到异常或者修改较差性能的代码。各种编程语言通常提供这样的工具，Java也不例外。分析Java代码的工具之一是 FindBugs。 它是开发资源工具，包含了一系列的规则来分析 Java concurrent 代码。

## 8.8 配置Eclipse来调试并发代码

(略)

## 8.9 配置NetBeans来调试并发代码

(略)

## 8.10 MultithreadedTC测试并发代码

MultithreadedTC 是一个 Java 库用来测试并发应用。它的主要目的是为了解决并发应用的不确定的问题。你不能控制他们的执行顺序。为了这个目睹，它包含了内部节拍器来控制应用的不同线程的执行顺序。这些测试线程作为类的方法来实现的。

[示例](../src/test/java/com/getset/j7cc/chapter8/ConcurrencyTesting.java#L208)学习如何使用 MultithreadedTC 库来为LinkedTransferQueue 实现一个测试。