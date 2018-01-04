# 第四章: 线程执行者

## 4.1 介绍 

通常，当你在Java中开发一个简单的并发编程应用程序，你会创建一些Runnable对象并创建相应的Thread对象来运行它们。如果你开发一个运行多个并发任务的程序，这种途径的缺点如下：

  * 你必须要实现很多相关代码来管理Thread对象（创建，结束，获得的结果）。
  * 你必须给每个任务创建一个Thread对象。如果你执行一个大数据量的任务，那么这可能影响应用程序的吞吐量。
  * 你必须有效地控制和管理计算机资源。如果你创建太多线程，会使系统饱和。

为了解决以上问题，从Java5开始JDK并发API提供一种机制。这个机制被称为Executor framework，接口核心是Executor，Executor的子接口是ExecutorService，而ThreadPoolExecutor类则实现了这两个接口。

这个机制将任务的创建与执行分离。使用执行者，你只要实现Runnable对象并将它们提交给执行者。执行者负责执行，实例化和运行这些线程。除了这些，它还可以使用线程池提高了性能。当你提交一个任务给这个执行者，它试图使用线程池中的线程来执行任务，从而避免继续创建线程。

Callable接口是Executor framework的另一个重要优点。它跟Runnable接口很相似，但它提供了两种改进，如下：

  * 这个接口中主要的方法叫call()，可以返回结果。
  * 当你提交Callable对象到执行者，你可以获取一个实现Future接口的对象，你可以用这个对象来控制Callable对象的状态和结果。

## 4.2 创建一个线程执行者 

使用Executor framework的第一步就是创建一个ThreadPoolExecutor类的对象。你可以使用这个类提供的4个构造器或Executors工厂类来 创建ThreadPoolExecutor。一旦有执行者，你就可以提交Runnable或Callable对象给执行者来执行。

[示例](../src/test/java/com/getset/j7cc/chapter4/ThreadExecutor.java#L16)如何使用这两种操作来实现一个web服务器的示例，这个web服务器用来处理各种客户端请求。

## 4.3 创建一个大小固定的线程执行者

当你使用由Executors类的 newCachedThreadPool()方法创建的基本ThreadPoolExecutor，你会有执行者运行在某一时刻的线程数的问题。这个执行者为每个接收到的任务创建一个线程（如果池中没有空闲的线程），所以，如果你提交大量的任务，并且它们有很长的（执行）时间，你会使系统过载和引发应用程序性能不佳的问题。

如果你想要避免这个问题，Executors类提供一个方法来创建大小固定的线程执行者。这个执行者有最大线程数。 如果你提交超过这个最大线程数的任务，这个执行者将不会创建额外的线程，并且剩下的任务将会阻塞，直到执行者有空闲线程。这种行为，保证执行者不会引发应用程序性能不佳的问题。

与上一节使用同一个[示例](../src/test/java/com/getset/j7cc/chapter4/ThreadExecutor.java#L16)。

## 4.4 执行者执行返回结果的任务 

Executor framework的一个优点是你可以并发执行返回结果的任务。Java并发API使用以下两种接口来实现：

  * Callable：此接口有一个call()方法。在这个方法中，你必须实现任务的（处理）逻辑。Callable接口是一个参数化的接口。意味着你必须表明call()方法返回的数据类型。
  * Future：此接口有一些方法来保证Callable对象结果的获取和管理它的状态。

[示例](../src/test/java/com/getset/j7cc/chapter4/ThreadExecutor.java#L37)演示如何实现返回结果的任务，并在执行者中运行它们。

## 4.5 运行多个任务并处理第一个结果 

ThreadPoolExecutor类中的invokeAny()方法接收任务数列，并启动它们，返回完成时没有抛出异常的第一个 任务的结果。该方法返回的数据类型与启动任务的call()方法返回的类型一样。

[示例](../src/test/java/com/getset/j7cc/chapter4/ThreadExecutor.java#L83)演示两个执行时间随机的Callable任务，首先执行结束的任务结果会返回，另一个任务会被中断。

## 4.6 运行多个任务并处理所有的结果

执行者框架允许你在不用担心线程创建和执行的情况下，并发的执行任务。它还提供了Future类，这个类可以用来控制任务的状态,也可以用来获得执行者执行任务的结果。

如果你想要等待一个任务完成，你可以使用以下两种方法：

  * 如果任务执行完成，Future接口的isDone()方法将返回true。
  * ThreadPoolExecutor类的awaitTermination()方法使线程进入睡眠，直到每一个任务调用shutdown()方法之后完成执行。
  
这两种方法都有一些缺点。第一个方法，你只能控制一个任务的完成。第二个方法，你必须等待一个线程来关闭执行者，否则这个方法的调用立即返回。

ThreadPoolExecutor类提供一个方法，允许你提交任务列表给执行者，并且在这个列表上等待所有任务的完成。

[示例](../src/test/java/com/getset/j7cc/chapter4/ThreadExecutor.java#L101)演示如何使用这个特性，实现一个示例，执行3个任务，并且当它们完成时将结果打印出来。

## 4.7 在延迟后执行者运行任务

执行者框架提供ThreadPoolExecutor类，使用池中的线程来执行Callable和Runnable任务，这样可以避免所有线程的创建操作。当你提交一个任务给执行者，会根据执行者的配置尽快执行它。在有些使用情况下，当你对尽快执行任务不感觉兴趣。你可能想要在一段时间之后执行任务或周期性地执行任务。基于这些目的，执行者框架提供 ScheduledThreadPoolExecutor类。

[示例](../src/test/java/com/getset/j7cc/chapter4/ThreadExecutor.java#L124)创建ScheduledThreadPoolExecutor并使用它安排任务在指定的时间后执行。

## 4.8 执行者定期的执行任务

执行者框架提供ThreadPoolExecutor类，使用池中的线程执行并发任务，从而避免所有线程的创建操作。当你提交任务给执行者，根据它的配置，它尽快地执行任务。当它结束，任务将被执行者删除，如果你想再次运行任务，你必须再次提交任务给执行者。

但是执行者框架通过ScheduledThreadPoolExecutor类可以执行周期性任务。

[示例](../src/test/java/com/getset/j7cc/chapter4/ThreadExecutor.java#L151)演示如何通过使用这个类的功能来安排一个周期性任务。

## 4.9 执行者取消任务 

当你使用执行者工作时，你不得不管理线程。你只实现Runnable或 Callable任务和把它们提交给执行者。执行者负责创建线程，在线程池中管理它们，当它们不需要时，结束它们。有时候，你想要取消已经提交给执行者 的任务。在这种情况下，你可以使用Future的cancel()方法，它允许你做取消操作。

[示例](../src/test/java/com/getset/j7cc/chapter4/ThreadExecutor.java#L176)演示如何使用这个方法来取消已经提交给执行者的任务。

## 4.10 执行者控制一个结束任务 

FutureTask类提供一个done()方法，允许你在执行者执行任务完成后执行一些代码。你可以用来做一些后处理操作，生成一个报告，通过e-mail发送结果，或释放一些资源。当执行的任务由FutureTask来控制完成，FutureTask会内部调用这个方法。这个方法在任务的结果设置和它的状态变成isDone状态之后被调用，不管任务是否已经被取消或正常完成。

[示例](../src/test/java/com/getset/j7cc/chapter4/ThreadExecutor.java#L176)默认情况下，这个方法是空的。你可以重写FutureTask类实现这个方法来改变这种行为。

如何重写这个方法，在任务完成之后执行代码。

## 4.11 执行者分离运行任务和处理结果 

通常，当你使用执行者执行并发任务时，你将会提交 Runnable或Callable任务给这个执行者，并获取Future对象控制这个方法。你可以发现这种情况，你需要提交任务给执行者在一个对象中，而处理结果在另一个对象中。基于这种情况，Java并发API提供CompletionService类。

CompletionService类整合了Executor和BlockingQueue的功能。你可以将Callable任务提交给它去执行，然后使用类似于队列中的take方法获取线程的返回值。在内部实现中，它使用Executor对象执行任务。使用CompletionService来维护处理线程不的返回结果时，主线程总是能够拿到最先完成的任务的返回值，而不管它们加入线程池的顺序。
     
这种行为的优点是共享一个CompletionService对象，并提交任务给执行者，这样其他（对象）可以处理结果。其局限性是，第二个对象只能获取那些已经完成它们的执行的任务的Future对象，所以，这些Future对象只能获取任务的结果。

[示例](../src/test/java/com/getset/j7cc/chapter4/ThreadExecutor.java#L207)演示使用CompletionService类把执行者启动任务和处理它们的结果分开。

## 4.12 执行者控制被拒绝的任务

当你想要结束执行者的执行，你使用shutdown()方法来表明它的结束。执行者等待正在运行或等待它的执行的任务的结束，然后结束它们的执行。

如果你在shutdown()方法和执行者结束之间，提交任务给执行者，这个任务将被拒绝，因为执行者不再接收新的任务。ThreadPoolExecutor类提供一种机制，在调用shutdown()后，不接受新的任务。

[示例](../src/test/java/com/getset/j7cc/chapter4/ThreadExecutor.java#L246)演示通过实现RejectedExecutionHandler，在执行者中管理拒绝任务。
