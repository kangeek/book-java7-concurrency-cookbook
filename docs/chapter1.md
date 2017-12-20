# [第一章: 线程管理](docs/chapter1.md)

## 1.1 介绍 

现代所有的操作系统都允许并发地执行任务。你可以在听音乐和浏览网页新闻的同时阅读邮件。我们说这种并发是**进程**级别的并发。而且在同一进程内，也会同时有多种任务。这些在同一进程内运行的并发任务称之为**线程**。

系统中有多个任务同时存在可称之为“**并发**”，系统内有多个任务同时执行可称之为“**并行**”；并发是并行的子集。比如在单核CPU系统上，只可能存在并发而不可能存在并行。

## 1.2 线程的创建和运行 

在Java中，我们有2个方式创建线程：
  * 通过直接继承thread类，然后覆盖run()方法。
  * 构建一个实现Runnable接口的类, 然后创建一个thread类对象并传递Runnable对象作为构造参数。

[示例](../src/test/java/com/getset/j7cc/chapter1/ThreadManagement.java#L12)使用第二种方法来制作一个简单的程序，它能创建和运行10个线程。每一个线程能计算和输出1-10以内的乘法表。

## 1.3 获取和设置线程信息 

Thread类的对象中保存了一些属性信息能够帮助我们来辨别每一个线程，知道它的状态，调整控制其优先级。 这些属性是：
  * ID: 每个线程的独特标识。
  * Name: 线程的名称。
  * Priority: 线程对象的优先级。优先级别在1-10之间，1是最低级，10是最高级。不建议改变它们的优先级，但是你想的话也是可以的。
  * Status: 线程的状态。在Java中，线程只能有这6种中的一种状态： new, runnable, blocked, waiting, time waiting, 或 terminated.

[示例](../src/test/java/com/getset/j7cc/chapter1/ThreadManagement.java#L26)是一个为10个线程设置名字和优先级的程序，然后展示它们的状态信息直到线程结束。这些线程会计算数字乘法表。

> 根据测试结果，在同等条件下，优先级只有在线程足够多或资源比较紧张的时候才能体现出来，而且起的是一个大致调控结果的作用，就是说，线程优先级越高，最先执行的概率就越大，最后执行完毕的概率就越小，但不保证它不会最后一个执行完毕 。

## 1.4 线程的中断 

一个多个线程在执行的Java程序，只有当其全部的线程执行结束时（更具体的说，是所有非守护线程结束或者某个线程调用System.exit()方法的时候），它才会结束运行。有时，你需要为了终止程序而结束一个线程，或者当程序的用户想要取消某个Thread对象正在做的任务。

Java提供中断机制来通知线程表明我们想要结束它。中断机制的特性是线程需要检查是否被中断，而且还可以决定是否响应结束的请求。所以，线程可以忽略中断请求并且继续运行。

[示例](../src/test/java/com/getset/j7cc/chapter1/ThreadManagement.java#L80)创建线程，然后在5秒之后，它会使用中断机制来强制结束线程。

关于`interrupt()`、`interrupted()`和`isInterrupted()`方法的具体说明，可以看一下例子[TestInterruptThread](src/main/java/com/getset/j7cc/chapter1/TestInterruptThread.java)。

## 1.5 操作线程的中断机制

在之前的指南里，你学习了如何中断执行线程和如何对Thread对象的中断控制。之前例子中的机制可以很容易中断的线程中使用。但是如果线程实现的是由复杂的算法分成的一些方法，或者它的方法有递归调用，那么我们可以用更好的机制来控制线程中断。为了这个Java提供了InterruptedException异常。当你检测到程序的中断并在run()方法内捕获，你可以抛这个异常。

[示例](../src/test/java/com/getset/j7cc/chapter1/ThreadManagement.java#L98)将实现的线程会根据给定的名称在文件件和子文件夹里查找文件，这个将展示如何使用InterruptedException异常来控制线程的中断。

## 1.6 线程的睡眠和恢复 

有时, 你会感兴趣在一段确定的时间内中断执行线程。例如, 程序的一个线程每分钟检查反应器状态。其余时间，线程什么也不做。在这段时间，线程不使用任何计算机资源。过了这段时间，当JVM选择它时，线程会准备好继续执行。为达此目的，你可以使用Thread类的 sleep() 方法 。此方法接收一个整数作为参数，表示线程暂停运行的毫秒数。 在调用sleep() 方法后，当时间结束时，当JVM安排他们CPU时间，线程会继续按指令执行，

另一种可能是使用一个有TimeUnit列举元素的sleep() 方法，使用线程类的 sleep() 方法让当前线程睡眠，但是它接收的参数单位是表示并转换成毫秒的。

[示例](../src/test/java/com/getset/j7cc/chapter1/ThreadManagement.java#L117)中, 我们将开发一个程序使用sleep()方法来每秒写入真实的日期。

Java 并发 API 有另一种方法能让线程对象离开 CPU。它是 yield() 方法, 它向JVM表示线程对象可以让CPU执行其他任务。JVM 不保证它会遵守请求。通常，它只是用来试调的。

## 1.7 等待线程的终结 

在某些情况下，我们需要等待线程的终结。例如，我们可能会遇到程序在执行前需要初始化资源。在执行剩下的代码之前，我们需要等待线程完成初始化任务。

为达此目的, 我们使用Thread 类的join() 方法。当前线程调用某个线程的这个方法时，它会暂停当前线程，直到被调用线程执行完成。

[示例](../src/test/java/com/getset/j7cc/chapter1/ThreadManagement.java#L136)中用初始化例子来使用这个方法。

Java 还提供2种形式的 join() 方法:
  * join (long milliseconds)
  * join (long milliseconds, long nanos)
既等待调用者线程的结束，由给定一个`milliseconds`毫秒的期限，无论调用者线程先结束还是期限先到，`join()`方法均会返回。

## 1.8 守护线程的创建和运行 

Java有一种特别的线程叫做守护线程。这种线程的优先级非常低，通常在程序里没有其他线程运行时才会执行它。当守护线程是程序里唯一在运行的线程时，JVM会结束守护线程并终止程序。

根据这些特点，守护线程通常用于在同一程序里给普通线程（也叫使用者线程）提供服务。它们通常无限循环的等待服务请求或执行线程任务。它们不能做重要的任务，因为我们不知道什么时候会被分配到CPU时间片，并且只要没有其他线程在运行，它们可能随时被终止。JAVA中最典型的这种类型代表就是垃圾回收器。

[示例](../src/test/java/com/getset/j7cc/chapter1/ThreadManagement.java#L157)中, 学习如何创建一个守护线程，开发一个用2个线程的例子；我们的使用线程会写事件到queue, 守护线程会清除queue里10秒前创建的事件。

## 1.9 处理线程的不受控制异常 

Java里有2种异常:
  * 检查异常（Checked exceptions）: 这些异常必须强制捕获它们或在一个方法里的throws子句中。 例如， IOException 或者ClassNotFoundException。
  * 未检查异常（Unchecked exceptions）: 这些异常不用强制捕获它们。例如， NumberFormatException。

在一个线程 对象的 run() 方法里抛出一个检查异常，我们必须捕获并处理他们。因为 run() 方法不接受 throws 子句。当一个非检查异常被抛出，默认的行为是在控制台写下stack trace并退出程序。

[示例](../src/test/java/com/getset/j7cc/chapter1/ThreadManagement.java#L175)演示如何捕获和处理线程对象抛出的未检测异常来避免程序终结。

## 1.10 使用本地线程变量

并发应用的一个关键地方就是共享数据。这个对那些扩展Thread类或者实现Runnable接口的对象特别重要。

如果你创建一个类对象，实现Runnable接口，然后多个Thread对象使用同样的Runnable对象，全部的线程都共享同样的属性。这意味着，如果你在一个线程里改变一个属性，全部的线程都会受到这个改变的影响。

有时，你希望程序里的各个线程的属性不会被共享。 Java 并发 API提供了一个很清楚的机制叫本地线程变量。

[示例](../src/test/java/com/getset/j7cc/chapter1/ThreadManagement.java#L188)程序用来描述在第一段话里的问题，和另一个程序使用本地线程变量机制解决这个问题。

## 1.11 线程组

Java并发 API里有个有趣的方法是把线程分组。这个方法允许我们按线程组作为一个单位来处理。例如，你有一些线程做着同样的任务，你想控制他们，无论多少线程还在运行，他们的状态会被一个call 中断。

Java 提供 ThreadGroup 类来组织线程。 ThreadGroup 对象可以由 Thread 对象组成和由另外的 ThreadGroup 对象组成,生成线程树结构。

[示例](../src/test/java/com/getset/j7cc/chapter1/ThreadManagement.java#L203)将开发一个简单的例子来演示 ThreadGroup 对象。我们有 10 个随机时间休眠的线程 (例如，模拟搜索)，然后当其中一个完成，就中断其余的。

## 1.12 处理线程组内的不受控制异常

1.9 介绍了如何使用通用方法来处理线程对象抛出的所有未捕获的异常。
       
[示例](../src/test/java/com/getset/j7cc/chapter1/ThreadManagement.java#L249)演示另一种方式：扩展一个`ThreadGroup`并重写`uncaughtException`方法，捕获所有被ThreadGroup类的任何线程抛出的非捕捉异常。

## 1.13 用线程工厂创建线程

在面向对象编程的世界中，工厂模式是最有用的设计模式。它是一个创造模式，还有它的目的是创建一个或几个类的对象的对象。然后，当我们想创建这些类的对象时，我们使用工厂来代替new操作。

有了这个工厂，我们有这些优势来集中创建对象们：

  * 更简单的改变了类的对象创建或者说创建这些对象的方式。
  * 更简单的为了限制的资源限制了对象的创建。 例如， 我们只new一个此类型的对象。
  * 更简单的生成创建对象的统计数据。
  * Java提供一个接口， ThreadFactory 接口实现一个线程对象工厂。 并发 API 使用线程工厂来创建线程的一些基本优势。

[示例](../src/test/java/com/getset/j7cc/chapter1/ThreadManagement.java#L264)演示了如何实现 ThreadFactory 接口来创建Thread 对象，当我们储存创建的线程对象时，可以取个性化的名字。