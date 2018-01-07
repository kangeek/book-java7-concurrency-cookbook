# 第七章: 定制并发类

## 7.1 介绍 

Java 并发API提供许多接口和类来实现并发应用程序。它们提供底层（low-level）机制，如Thread类、Runnable或Callable接口、或synchronized关键字。同样也提供高级（high-level）机制，如Executor框架和Java 7 发布的Fork/Join框架。尽管这样，你可能发现你自己开发一个程序时，没有一个java类能满足你的需求。

在这种情况下，你也许需要基于Java提供的（API）实现自己定制的并发工具。基本上，你可以：

  * 实现一个接口提供那个接口定义的功能。比如：ThreadFactory接口。
  * 覆盖一个类的一些方法来调整它的行为以满足你的需求。比如，覆盖Thread类的run()方法，默认情况下，它没有用并且应该被覆盖以提供一些功能。

通过这个文章的指南，你将学习如何改变一些Java并发API类的行为，而不必从头开始设计一个并发框架。你可以使用这些指南作为初始点来实现你自己的定制。

## 7.2 定制ThreadPoolExecutor 类

执行者框架（Executor framework）是一种机制，允许你将线程的创建与执行分离。它是基于Executor和ExecutorService接口及其实现这两个接口的ThreadPoolExecutor类。它有一个内部的线程池和提供允许你提交两种任务给线程池执行的方法。这些任务是：

Runnable接口，实现没有返回结果的任务
Callable接口，实现返回结果的任务
在这两种情况下，你只有提交任务给执行者。这个执行者使用线程池中的线程或创建一个新的线程来执行这些任务。执行者同样决定任务被执行的时刻。

[示例](../src/test/java/com/getset/j7cc/chapter7/CustomizedConcurrenty.java#L13)演示覆盖ThreadPoolExecutor类的一些方法，计算你在执行者中执行的任务的执行时间，并且将关于执行者完成它的执行的统计信息写入到控制台。

## 7.3 实现一个优先级制的执行者类 

在Java并发API的第一个版本中，你必须创建和运行应用程序中的所有线程。在Java版本5中，随着执行者框架（Executor framework）的出现，对于并发任务的执行，一个新的机制被引进。

使用执行者框架（Executor framework），你只要实现你的任务并把它们提交给执行者。这个执行者负责执行你的任务的线程的创建和执行。

在内部，一个执行者使用一个阻塞队列来存储待处理任务。以任务到达执行者的顺序来存储。一个可能的替代就是使用一个优先级列队来存储新的任务。这样，如果一个高优先级的新任务到达执行者，它将比其他已经在等待一个线程来执行它们，且低优先级的任务先执行。

[示例](../src/test/java/com/getset/j7cc/chapter7/CustomizedConcurrenty.java#L40)中执行者使用优先级队列来存储提交执行的任务。

## 7.4 实现ThreadFactory接口来生成自定义线程 

在面向对象编程的世界中，工厂模式（factory pattern）是一个被广泛使用的设计模式。它是一个创建模式，它的目的是开发一个类，这个类的使命是创建一个或多个类的对象。然后，当我们要创建一个类的一个对象时，我们使用这个工厂而不是使用new操作。

使用这个工厂，我们集中对象的创建，获取容易改变创建对象的类的优势，或我们创建这些对象的方式，容易限制创建对象的有限资源。比如，我们只能有一个类型的N个对象，就很容易产生关于对象创建的统计数据。
Java提供ThreadFactory接口，用来实现一个Thread对象工厂。Java并发API的一些高级工具，如执行者框架（Executor framework）或Fork/Join框架（Fork/Join framework），使用线程工厂创建线程。

在Java并发API中的其他工厂模式的例子是Executors类。它提供许多方法来创建不同类型的Executor对象。

[示例](../src/test/java/com/getset/j7cc/chapter7/CustomizedConcurrenty.java#L54)中扩展了自定义的Thread和ThreadFactory。

## 7.5 在执行者对象中使用我们的 ThreadFactory 

在前面的指南中，实现ThreadFactory接口生成自定义线程，我们引进了工厂模式和提供如何实现一个实现ThreadFactory接口的线程的工厂例子。

执行者框架（Executor framework）是一种机制，它允许你将线程的创建与执行分离。它是基于Executor、ExecutorService接口和实现这两个接口的ThreadPoolExecutor类。它有一个内部的线程池和提供一些方法，这些方法允许你提交两种任务给线程池执行。这两种任务是：

  * 实现Runnable接口的类，用来实现没有返回结果的任务
  * 实现Callable接口的类，用来实现有返回结果的任务

在执行者框架（Executor framework）的内部，它提供一个ThreadFactory接口来创建线程，这是用来产生新的线程。

[示例](../src/test/java/com/getset/j7cc/chapter7/CustomizedConcurrenty.java#L75)实现你自己的线程类，用一个工厂来创建这个类的线程，及如何在执行者中使用这个工厂，所以这个执行者将执行你的线程。

## 7.6 在计划好的线程池中定制运行任务

计划的线程池是 Executor 框架的基本线程池的扩展，允许你定制一个计划来执行一段时间后需要被执行的任务。 它通过 ScheduledThreadPoolExecutor 类来实现，并允许运行以下这两种任务：

  * Delayed 任务：这种任务在一段时间后仅执行一次。
  * Periodic 任务：这种任务在延迟后执行，然后通常周期性运行。

Delayed 任务可以执行 Callable 和 Runnable 对象，但是 periodic任务只能执行 Runnable 对象。全部任务通过计划池执行的都必须实现 RunnableScheduledFuture 接口。

[示例](../src/test/java/com/getset/j7cc/chapter7/CustomizedConcurrenty.java#L100)实现自定义的 RunnableScheduledFuture 接口来执行延迟和周期性任务，任务执行前后输出一些信息。

## 7.7 实现ThreadFactory接口来生成自定义线程给Fork/Join框架 

Fork/Join框架是Java7中最有趣的特征之一。它是Executor和ExecutorService接口的一个实现，允许你执行Callable和Runnable任务而不用管理这些执行线程。

这个执行者面向执行能被拆分成更小部分的任务。主要组件如下：

  * 一个特殊任务，实现ForkJoinTask类
  * 两种操作，将任务划分成子任务的fork操作和等待这些子任务结束的join操作
  * 一个算法，优化池中线程的使用的work-stealing算法。当一个任务正在等待它的子任务（结束）时，它的执行线程将执行其他任务（等待执行的任务）。

ForkJoinPool类是Fork/Join的主要类。在它的内部实现，有如下两种元素：

  * 一个存储等待执行任务的列队。
  * 一个执行任务的线程池

[示例](../src/test/java/com/getset/j7cc/chapter7/CustomizedConcurrenty.java#L131)一个在ForkJoinPool类中使用的自定义的ForkJoinWorkerThread（用来统计自己承担的工作数量），WorkThread用自定义的工厂传递给ForkJoinPool。

## 7.8 在Fork/Join框架中定制运行任务 

执行者框架分开了任务的创建和运行。这样，你只要实现 Runnable 对象来使用 Executor 对象。你可以发送 Runnable 任务给执行者，然后它会创建，管理，并终结必要的线程来执行这些任务。

Java 7 在 Fork/Join 框架中提供了特殊的执行者。这个框架是设计用来解决那些可以使用 divide 和 conquer 技术分成更小点的任务的问题。在一个任务内，你要检查你要解决的问题的大小，如果它比设定的大小还大，你就把问题分成2个或多个任务，再使用框架来执行这些任务。

如果问题的大小比设定的大小要小，你可以在任务中直接解决问题，可选择返回结果。Fork/Join 框架实现 work-stealing 算法来提高这类问题的整体表现。

Fork/Join 框架的主要类是 ForkJoinPool 类。它内部有以下2个元素：

  * 一个等待执行的任务queue
  * 一个执行任务的线程池

默认情况，被 ForkJoinPool类执行的任务是 ForkJoinTask 类的对象。你也可以发送 Runnable 和 Callable 对象给 ForkJoinPool 类，但是他们就不能获得所以 Fork/Join 框架的好处。通常情况，你将发送ForkJoinTask 类的这2个子类中的一个给 ForkJoinPool 对象：

  * RecursiveAction: 如果你的任务没有返回结果
  * RecursiveTask: 如果你的任务返回结果

[示例](../src/test/java/com/getset/j7cc/chapter7/CustomizedConcurrenty.java#L154)为 Fork/Join 框架加入定制化的任务，扩展ForkJoinTask类。定制化的任务可以计量运行时间并写入控制台台，从而可以控制它的进展。当然也可以实现你自己的 Fork/Join 任务来写日志信息，为了获得在这个任务中使用的资源，或者来 post-process 任务的结果。

## 7.9 实现一个自定义锁类

锁是Java并发API提供的基本同步机制之一。它允许程序员保护代码的临界区，所以，在某个时刻只有一个线程能执行这个代码块。它提供以下两种操作：

  * lock()：当你想要访问一个临界区时，调用这个方法。如果有其他线程正在运行这个临界区，其他线程将阻塞，直到它们被这个锁唤醒，从而获取这个临界区的访问。
  * unlock()：你在临界区的尾部调用这个方法，允许其他线程访问这个临界区。

在Java并发API中，锁是在Lock接口及其一些实现类中声明的，比如ReentrantLock类。

[示例](../src/test/java/com/getset/j7cc/chapter7/CustomizedConcurrenty.java#L211)自定义实现Lock对象，它实现了Lock接口并可用来保护临界区的类。

## 7.10 实现一个基于优先级传输Queue 

Java 7 API 提供几种与并发应用相关的数据类型。从这里面，我们想来重点介绍以下2种数据类型：

  * LinkedTransferQueue：这个数据类型支持那些有生产者和消费者结构的程序。 在那些应用，你有一个或者多个数据生产者，一个或多个数据消费者和一个被生产者和消费者共享的数据类型。生产者把数据放入数据结构内，然后消费者从数据结构内提取数据。如果数据结构为空，消费者会被阻塞直到有数据可以消费。如果数据结构满了，生产者就会被阻塞直到有空位来放数据。
  * PriorityBlockingQueue：在这个数据结构，元素是按照顺序储存的。元素们必须实现 带有 compareTo() 方法的 Comparable 接口。当你在结构中插入数据时，它会与数据元素对比直到找到它的位置。

LinkedTransferQueue 的元素是按照抵达顺序储存的，所以越早到的越先被消耗。你有可能需要开发 producer/ consumer 程序，它的消耗顺序是由优先级决定的而不是抵达时间。

[示例](../src/test/java/com/getset/j7cc/chapter7/CustomizedConcurrenty.java#L255)实现在 producer/ consumer 问题中使用的数据结构，这些元素将被按照他们的优先级排序，级别高的会先被消费。

## 7.11 实现你自己的原子对象

Java版本5中引入原子变量，并提供对单个变量的原子操作。当一个线程在原子变量上执行操作时，这个类的实现包含一种机制用来检查这个操作在一个步骤内完成。基本上，这个操作是先获取变量的值，然后在本地变量中改变这个值，最后尝试将旧值变成这个新值。如果旧值仍然是相同的，它将改变成新值，否则，这个方法重新开始这个操作。（校对注：这段话描述了CAS的实现原理 ）

[示例](../src/test/java/com/getset/j7cc/chapter7/CustomizedConcurrenty.java#L287)通过扩展 AtomicInteger 实现了自定义的 AtomicInteger， 可以记录“Compare and Set”的失败次数。