# 第五章: Fork/Join 框架

## 5.1 介绍 

通常，当你实现一个简单的并发应用程序，你实现一些Runnable对象和相应的 Thread对象。在你的程序中，你控制这些线程的创建、执行和状态。Java 5引入了Executor和ExecutorService接口及其实现类进行了改进（比如：ThreadPoolExecutor类）。

执行者框架将任务的创建与执行分离。有了它，你只要实现Runnable对象和使用Executor对象。你提交Runnable任务给执行者，它创建、管理线程来执行这些任务。

Java 7更进一步，包括一个面向特定问题的ExecutorService接口的额外实现，它就是Fork/Join框架。


这个框架被设计用来解决可以使用分而治之技术将任务分解成更小的问题。在一个任务中，检查你想要解决问题的大小，如果它大于一个既定的大小，把它分解成更小的任务，然后用这个框架来执行。如果问题的大小是小于既定的大小，你直接在任务中解决这问题。它返回一个可选地结果。以下图总结了这个概念：

![fork/join](http://ifeve.com/wp-content/uploads/2013/08/1.jpg)

没有公式来确定问题的参数大小，所以你可以根据它的特点来确定一个任务是否可以被细分。你可以参考任务处理元素的大小和预估任务执行时间来确定子任务大小。你需要解决的问题是测试不同的参考大小来选择最好的一个。你可以将ForkJoinPool作为一种特殊的执行者来考虑。

这个框架基于以下两种操作：

  * fork操作：当你把任务分成更小的任务和使用这个框架执行它们。
  * join操作：当一个任务等待它创建的任务的结束。

Fork/Join 和Executor框架主要的区别是work-stealing算法。不像Executor框架，当一个任务正在等待它使用join操作创建的子任务的结 束时，执行这个任务的线程（工作线程）查找其他未被执行的任务并开始它的执行。通过这种方式，线程充分利用它们的运行时间，从而提高了应用程序的性能。

为实现这个目标，Fork/Join框架执行的任务有以下局限性：

  * 任务只能使用fork()和join()操作，作为同步机制。如果使用其他同步机制，工作线程不能执行其他任务，当它们在同步操作时。比如，在Fork/Join框架中，你使任务进入睡眠，正在执行这个任务的工作线程将不会执行其他任务，在这睡眠期间内。
  * 任务不应该执行I/O操作，如读或写数据文件。
  * 任务不能抛出检查异常，它必须包括必要的代码来处理它们。
  
Fork/Join框架的核心是由以下两个类：

  * ForkJoinPool：它实现ExecutorService接口和work-stealing算法。它管理工作线程和提供关于任务的状态和它们执行的信息。
  * ForkJoinTask： 它是将在ForkJoinPool中执行的任务的基类。它提供在任务中执行fork()和join()操作的机制，并且这两个方法控制任务的状态。通常， 为了实现你的Fork/Join任务，你将实现两个子类的子类的类： RecursiveAction 用于没有返回结果的任务和 RecursiveTask 用于有返回结果的任务。

## 5.2 创建 Fork/Join 池 

在这个指南中，你将学习如何使用Fork/Join框架的基本元素。它包括：

  * 创建一个ForkJoinPool对象来执行任务。
  * 创建一个ForkJoinPool执行的ForkJoinTask类。

你将在这个示例中使用Fork/Join框架的主要特点，如下：

  * 你将使用默认构造器创建ForkJoinPool。
  * 在这个任务中，你将使用Java API文档推荐的结构：
  
```
    If (problem size < default size){
        tasks=divide(task);
        execute(tasks);
    } else {
        resolve problem using another algorithm;
    }
```

  * 你将以一种同步方式执行任务。当一个任务执行2个或2个以上的子任务时，它将等待它们的结束。通过这种方式 ，正在执行这些任务的线程（工作线程）将会查找其他任务（尚未执行的任务）来执行，充分利用它们的执行时间。
  * 你将要实现的任务将不会返回任何结果，所以你将使用RecursiveAction作为它们实现的基类。
  
即使ForkJoinPool类被设计成用来执行一个ForkJoinTask，你也可以直接执行Runnable和Callable对象。你也可以使用ForkJoinTask类的adapt()方法来执行任务，它接收一个Callable对象或Runnable对象（作为参数）并返回一个ForkJoinTask对象。

[示例](../src/test/java/com/getset/j7cc/chapter5/ForkJoinFramework.java#L13)演示了使用Fork/Join框架将任务拆分为子任务并多线程执行。

## 5.3 加入任务的结果 

Fork/Join框架提供了执行返回一个结果的任务的能力。这些任务的类型是实现了RecursiveTask类。这个类继承了ForkJoinTask类和实现了执行者框架提供的Future接口。

在任务中，你必须使用Java API方法推荐的结构：

```
    If (problem size < size){
        tasks=Divide(task);
        execute(tasks);
        groupResults()
        return result;
    } else {
        resolve problem;
        return result;
    }
```

如果这个任务必须解决一个超过预定义大小的问题，你应该将这个任务分解成更多的子任务，并且用Fork/Join框架来执行这些子任务。当这些子任务完成执行，发起的任务将获得所有子任务产生的结果 ，对这些结果进行分组，并返回最终的结果。最终，当在池中执行的发起的任务完成它的执行，你将获取整个问题地最终结果。

[示例](../src/test/java/com/getset/j7cc/chapter5/ForkJoinFramework.java#L98)是一个在二维数组中统计元素出现次数的例子，用分而治之的方式拆分任务，子任务会返回结果。

## 5.4 异步运行任务 

当你在ForkJoinPool中执行ForkJoinTask时，你可以使用同步或异步方式来实现。当你使用同步方式时，提交任务给池的方法直到提交的任务完成它的执行，才会返回结果。当你使用异步方式时，提交任务给执行者的方法将立即返回，所以这个任务可以继续执行。

你应该意识到这两个方法有很大的区别，当你使用同步方法，调用这些方法（比如：invokeAll()方法）的任务将被阻塞，直到提交给池的任务完成它的执行。这允许ForkJoinPool类使用work-stealing算法，分配一个新的任务给正在执行睡眠任务的工作线程。反之，当你使用异步方法（比如：fork()方法），这个任务将继续它的执行，所以ForkJoinPool类不能使用work-stealing算法来提高应用程序的性能。在这种情况下，只有当你调用join()或get()方法来等待任务的完成时，ForkJoinPool才能使用work-stealing算法。

[示例](../src/test/java/com/getset/j7cc/chapter5/ForkJoinFramework.java#L98)与5.3使用了同一个例子，通过任务构造方法参数指定使用同步还是异步方式运行。

## 5.5 任务中抛出异常

在Java中有两种异常：

  * 已检查异常（Checked exceptions）：这些异常必须在一个方法的throws从句中指定或在内部捕捉它们。比如：IOException或ClassNotFoundException。
  * 未检查异常（Unchecked exceptions）：这些异常不必指定或捕捉。比如：NumberFormatException。

在ForkJoinTask类的compute()方法中，你不能抛出任何已检查异常，因为在这个方法的实现中，它没有包含任何抛出（异常）声明。你必须包含必要的代码来处理异常。但是，你可以抛出（或者它可以被任何方法或使用内部方法的对象抛出）一个未检查异常。ForkJoinTask和ForkJoinPool类的行为与你可能的期望不同。程序不会结束执行，并且你将不会在控制台看到任何关于异常的信息。它只是被吞没，好像它没抛出（异常）。你可以使用ForkJoinTask类的一些方法，得知一个任务是否抛出异常及其异常种类。

[示例](../src/test/java/com/getset/j7cc/chapter5/ForkJoinFramework.java#L13)与5.1使用了同一个例子，任务执行过程中某个子任务会出现除数为零异常。

## 5.6 取消任务

当你在一个ForkJoinPool类中执行ForkJoinTask对象，在它们开始执行之前，你可以取消执行它们。ForkJoinTask类提供cancel()方法用于这个目的。当你想要取消一个任务时，有一些点你必须考虑一下，这些点如下：

  * ForkJoinPool类并没有提供任何方法来取消正在池中运行或等待的所有任务。
  * 当你取消一个任务时，你不能取消一个已经执行的任务。
  
ForkJoinTask提供cancel()方法，允许你取消一个还未执行的任务。这是一个非常重要的点。如果任务已经开始它的执行，那么调用cancel()方法对它没有影响。这个方法接收一个Boolean值，名为mayInterruptIfRunning的参数。这个名字可能让你觉得，如果你传入一个true值给这个方法，这个任务将被取消，即使它正在运行。

Java API文档指出，在ForkJoinTask类的默认实现中，这个属性不起作用。任务只能在它们还未开始执行时被取消。一个任务的取消不会影响到已经提到到池的（其他）任务。它们继续它们的执行。 Fork/Join框架的一个局限性是，它不允许取消在ForkJoinPool中的所有任务。为了克服这个限制，你实现了TaskManager类。它存储被提到池中的所有任务。它有一个方法取消它存储的所有任务。如果一个任务由于它正在运行或已经完成而不能被取消，cancel()方法返回false值，所以，你可以尝试取消所有任务，而不用担心可能有间接的影响。 