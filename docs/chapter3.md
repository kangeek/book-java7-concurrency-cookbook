# [第三章: 线程同步工具](docs/chapter3.md)

## 3.1 介绍

在第二章基本的线程同步中，我们学习了同步和critical section的内容。基本上，当多个并发任务共享一个资源时就称为同步，例如：一个对象或者一个对象的属性。访问这个资源的代码块称为：临界区。

如果机制没有使用恰当，那么可能会导致错误的结果，或者数据不一致，又或者出现异常情况。所以必须采取java语言提供的某个恰当的同步机制来避免这些问题。

在第二章，基本的线程同步中，我们学会了以下2个同步机制：
  * 关键词同步
  * Lock接口和它的实现类们：ReentrantLock, ReentrantReadWriteLock.ReadLock, 和 ReentrantReadWriteLock.WriteLock
  
在此章节，我们将学习怎样使用高等级的机制来达到多线程的同步。这些高等级机制有：
  * Semaphores: 控制访问多个共享资源的计数器。此机制是并发编程的最基本的工具之一，而且大部分编程语言都会提供此机制。
  * CountDownLatch: CountDownLatch 类是Java语言提供的一个机制，它允许线程等待多个操作的完结。
  * CyclicBarrier: CyclicBarrier 类是又一个java语言提供的机制，它允许多个线程在同一个点同步。
  * Phaser: Phaser类是又一个java语言提供的机制，它控制并发任务分成段落来执行。全部的线程在继续执行下一个段之前必须等到之前的段执行结束。这是Java 7 API的一个新特性。
  * Exchanger: Exchanger类也是java语言提供的又一个机制，它提供2个线程间的数据交换点。

Semaphores是最基本的同步机制可以用来在任何问题中保护任何critical section。其他的机制只有在之前描述的那些有特殊特点的应用中使用。请根据你的应用的特点来选择适当的机制。

这章有7个教你如何使用以上描述的机制的指南。

## 3.2 控制并发访问一个资源

这一节，学习怎样使用Java语言提供的Semaphore机制。Semaphore是一个控制访问多个共享资源的计数器。

Semaphore的内容是由Edsger Dijkstra引入并在 THEOS操作系统上第一次使用。

当一个线程想要访问某个共享资源，首先，它必须获得semaphore。如果semaphore的内部计数器的值大于0，那么semaphore减少计数器的值并允许访问共享的资源。计数器的值大于0表示，有可以自由使用的资源，所以线程可以访问并使用它们。

另一种情况，如果semaphore的计数器的值等于0，那么semaphore让线程进入休眠状态一直到计数器大于0。计数器的值等于0表示全部的共享资源都正被线程们使用，所以此线程想要访问就必须等到某个资源成为自由的。

当线程使用完共享资源时，他必须放出semaphore为了让其他线程可以访问共享资源。这个操作会增加semaphore的内部计数器的值。

[示例](../src/test/java/com/getset/j7cc/chapter3/ThreadSyncUtilities.java#L14)使用Semaphore类来实现一种比较特殊的semaphores种类，称为binary semaphores。这个semaphores种类保护访问共享资源的独特性，所以semaphore的内部计数器的值只能是1或者0。为了展示如何使用它，你将要实现一个PrintQueue类来让并发任务打印它们的任务。这个PrintQueue类会受到binary semaphore的保护，所以每次只能有一个线程可以打印。

Semaphore类有另2个版本的 acquire() 方法：

  * acquireUninterruptibly()：acquire()方法是当semaphore的内部计数器的值为0时，阻塞线程直到semaphore被释放。在阻塞期间，线程可能会被中断，然后此方法抛出InterruptedException异常。而此版本的acquire方法会忽略线程的中断而且不会抛出任何异常。
  * tryAcquire()：此方法会尝试获取semaphore。如果成功，返回true。如果不成功，返回false值，并不会被阻塞和等待semaphore的释放。接下来是你的任务用返回的值执行正确的行动。

## 3.3 控制并发访问多个资源

上一节的binary semaphores是用来保护访问一个共享资源的，或者说一个代码片段每次只能被一个线程执行。但是semaphores也可以用来保护多个资源的副本，也就是说当你有一个代码片段每次可以被多个线程执行。

这一节中我们使用semaphore来保护多个资源副本。[示例](../src/test/java/com/getset/j7cc/chapter3/ThreadSyncUtilities.java#L45)有一个print queue但可以在3个不同的打印机上打印文件。

The acquire(), acquireUninterruptibly(), tryAcquire(),和release()方法有一个外加的包含一个int参数的版本。这个参数表示 线程想要获取或者释放semaphore的许可数。也可以这样说，这个线程想要删除或者添加到semaphore的内部计数器的单位数量。在这个例子中acquire(), acquireUninterruptibly(), 和tryAcquire() 方法, 如果计数器的值小于许可值，那么线程就会被阻塞直到计数器到达或者大于许可值。

## 3.4 等待多个并发事件完成

Java并发API提供这样的类，它允许1个或者多个线程一直等待，直到一组操作执行完成。 这个类就是CountDownLatch类。它初始一个整数值，此值是线程将要等待的操作数。当某个线程为了想要执行这些操作而等待时， 它要使用 await()方法。此方法让线程进入休眠直到操作完成。 当某个操作结束，它使用countDown() 方法来减少CountDownLatch类的内部计数器。当计数器到达0时，这个类会唤醒全部使用await() 方法休眠的线程们。

[示例](../src/test/java/com/getset/j7cc/chapter3/ThreadSyncUtilities.java#L68)演示如何使用 CountDownLatch 类来实现 video-conference 系统。 video-conference 系统将等待全部参与者到达后才会开始。

CountDownLatch类有3个基本元素：

  1. 初始值决定CountDownLatch类需要等待的事件的数量。
  2. await() 方法, 被等待全部事件终结的线程调用。
  3. countDown() 方法，事件在结束执行后调用。
  
当创建 CountDownLatch 对象时，对象使用构造函数的参数来初始化内部计数器。每次调用 countDown() 方法, CountDownLatch 对象内部计数器减一。当内部计数器达到0时， CountDownLatch 对象唤醒全部使用 await() 方法睡眠的线程们。

不可能重新初始化或者修改CountDownLatch对象的内部计数器的值。一旦计数器的值初始后，唯一可以修改它的方法就是之前用的 countDown() 方法。当计数器到达0时， 全部调用 await() 方法会立刻返回，接下来任何countDown() 方法的调用都将不会造成任何影响。

此方法与其他同步方法有这些不同：CountDownLatch 机制不是用来保护共享资源或者临界区。它是用来同步一个或者多个执行多个任务的线程。它只能使用一次。像之前解说的，一旦CountDownLatch的计数器到达0，任何对它的方法的调用都是无效的。如果你想再次同步，你必须创建新的对象。

## 3.5 在一个相同点同步任务

Java 并发 API 提供了可以允许2个或多个线程在在一个确定点的同步应用。它是 CyclicBarrier 类。此类与在此章节的等待多个并发事件完成指南中的 CountDownLatch 类相似，但是它有一些特殊性让它成为更强大的类。

CyclicBarrier 类有一个整数初始值，此值表示将在同一点同步的线程数量。当其中一个线程到达确定点，它会调用await() 方法来等待其他线程。当线程调用这个方法，CyclicBarrier阻塞线程进入休眠直到其他线程到达。当最后一个线程调用CyclicBarrier 类的await() 方法，它唤醒所有等待的线程并继续执行它们的任务。

CyclicBarrier 类有个有趣的优势是，你可以传递一个外加的 Runnable 对象作为初始参数，并且当全部线程都到达同一个点时，CyclicBarrier类 会把这个对象当做线程来执行。此特点让这个类在使用 divide 和 conquer 编程技术时，可以充分发挥任务的并行性，

[示例](../src/test/java/com/getset/j7cc/chapter3/ThreadSyncUtilities.java#L82)使用 CyclicBarrier 类来让一组线程在一个确定点同步。并使用 Runnable 对象，在全部线程都到达确定点后被执行。在这个例子里，你将在数字矩阵中查找一个数字。矩阵会被分成多个子集（使用divide 和 conquer 技术），所以每个线程会在一个子集中查找那个数字。一旦全部行程运行结束，会有一个最终任务来统一他们的结果。

## 3.6 运行并发阶段性任务

Java 并发 API 提供的一个非常复杂且强大的功能是，能够使用Phaser类运行阶段性的并发任务。当某些并发任务是分成多个步骤来执行时，那么此机制是非常有用的。Phaser类提供的机制是在每个步骤的结尾同步线程，所以除非全部线程完成第一个步骤，否则线程不能开始进行第二步。

相对于其他同步应用，我们必须初始化Phaser类与这次同步操作有关的任务数，我们可以通过增加或者减少来不断的改变这个数。

[示例](../src/test/java/com/getset/j7cc/chapter3/ThreadSyncUtilities.java#L113)使用Phaser类来同步3个并发任务。这3个任务会在3个不同的文件夹和它们的子文件夹中搜索扩展名是.log并在24小时内修改过的文件。这个任务被分成3个步骤：


The Phaser类还提供了其他相关方法来改变phase。他们是：

  * arrive(): 此方法示意phaser某个参与者已经结束actual phase了，但是他应该等待其他的参与者才能继续执行。小心使用此法，因为它并不能与其他线程同步。
  * awaitAdvance(int phase): 如果我们传递的参数值等于phaser的actual phase，此方法让当前线程进入睡眠直到phaser的全部参与者结束当前的phase。如果参数值与phaser 的 actual phase不等，那么立刻返回。
  * awaitAdvanceInterruptibly(int phaser): 此方法等同与之前的方法，只是在线程正在此方法中休眠而被中断时候，它会抛出InterruptedException 异常。
  
**Phaser的参与者的注册**

当你创建一个 Phaser 对象,你表明了参与者的数量。但是Phaser类还有2种方法来增加参与者的数量。他们是：

  * register(): 此方法为Phaser添加一个新的参与者。这个新加入者会被认为是还未到达 actual phase.
  * bulkRegister(int Parties): 此方法为Phaser添加一个特定数量的参与者。这些新加入的参与都会被认为是还未到达 actual phase.
  * Phaser类提供的唯一一个减少参与者数量的方法是arriveAndDeregister() 方法，它通知phaser线程已经结束了actual phase,而且他不想继续phased的操作了。

**强制终止 Phaser**

当phaser有0个参与者，它进入一个称为Termination的状态。Phaser 类提供 forceTermination() 来改变phaser的状态，让它直接进入Termination 状态，不在乎已经在phaser中注册的参与者的数量。此机制可能会很有用在一个参与者出现异常的情况下来强制结束phaser.

当phaser在 Termination 状态， awaitAdvance() 和 arriveAndAwaitAdvance() 方法立刻返回一个负值，而不是一般情况下的正值如果你知道你的phaser可能终止了，那么你可以用这些方法来确认他是否真的终止了。

## 3.7 控制并发阶段性任务的改变

Phaser 类提供每次phaser改变阶段都会执行的方法。它是 onAdvance() 方法。它接收2个参数：当前阶段数和注册的参与者数；它返回 Boolean 值，如果phaser继续它的执行，则为 false；否则为真，即phaser结束运行并进入 termination 状态。

如果注册参与者为0，此方法的默认的实现值为真，要不然就是false。如果你扩展Phaser类并覆盖此方法，那么你可以修改它的行为。通常，当你要从一个phase到另一个，来执行一些行动时，你会对这么做感兴趣的。

[示例](../src/test/java/com/getset/j7cc/chapter3/ThreadSyncUtilities.java#L145)演示如何控制phaser的 phase的改变，通过实现自定义版本的 Phaser类并覆盖 onAdvance() 方法来执行一些每个phase 都会改变的行动。你将要实现一个模拟测验，有些学生要完成他们的练习。全部的学生都必须完成同一个练习才能继续下一个练习。

## 3.8 在并发任务间交换数据

Java 并发 API 提供了一种允许2个并发任务间相互交换数据的同步应用。更具体的说，Exchanger 类允许在2个线程间定义同步点，当2个线程到达这个点，他们相互交换数据类型，使用第一个线程的数据类型变成第二个的，然后第二个线程的数据类型变成第一个的。

这个类在遇到类似生产者和消费者问题时，是非常有用的。来一个非常经典的并发问题：你有相同的数据buffer，一个或多个数据生产者，和一个或多个数据消费者。只是Exchange类只能同步2个线程，所以你只能在你的生产者和消费者问题中只有一个生产者和一个消费者时使用这个类。

[示例](../src/test/java/com/getset/j7cc/chapter3/ThreadSyncUtilities.java#L182)演示如何使用 Exchanger 类来解决只有一个生产者和一个消费者的生产者和消费者问题。