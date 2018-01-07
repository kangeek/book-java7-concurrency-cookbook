# [第六章: 并发集合](docs/chapter6.md)

## 6.1 介绍 

在编程中，数据结构是一种基本的元素。几乎每个程序都使用一个或多个数据结构类型来存储和管理它们的数据。Java API提供了Java集合框架（Java Collections framework），它包括可以用来实现许多不同的数据结构的接口、类和算法，你可以在程序中使用它们。

当你需要在并发程序中使用数据集合时，你必须十分小心的选择实现。大多数集合数并不适合用在并发应用程序中，因为它们没有控制并发访问数据。如果一些并发任务共享一个数据结构，而这个数据结构并不适合用在并发任务中，你将会有数据不一致的错误，这将影响到程序的正确运行。ArrayList类就是这种数据结构的一个例子。

Java提供了你可以在你的并发程序中使用的，而且不会有任何问题或不一致的数据集合。基本上，Java提供两种在并发应用程序中使用的集合：

  * 阻塞集合：这种集合包括添加和删除数据的操作。如果操作不能立即进行，是因为集合已满或者为空，该程序将被阻塞，直到操作可以进行。
  * 非阻塞集合：这种集合也包括添加和删除数据的操作。如果操作不能立即进行，这个操作将返回null值或抛出异常，但该线程将不会阻塞。
  
通过本章的指南，你将学习如何使用一些可以用在并发应用程序中的Java集合。这包括：

  * 非阻塞列表，使用ConcurrentLinkedDeque类。
  * 阻塞列表，使用LinkedBlockingDeque类。
  * 用在生产者与消费者数据的阻塞列表，使用LinkedTransferQueue类。
  * 使用优先级排序元素的阻塞列表，使用PriorityBlockingQueue类。
  * 存储延迟元素的阻塞列表，使用DelayQueue类。
  * 非阻塞可导航的map，使用ConcurrentSkipListMap类。
  * 随机数，使用ThreadLocalRandom类
  * 原子变量，使用AtomicLong和AtomicIntegerArray类

## 6.2 使用非阻塞线程安全列表 

列表（list）是最基本的集合。一个列表有不确定的元素数量，并且你可以添加、读取和删除任意位置上的元素。并发列表允许不同的线程在同一时刻对列表的元素进行添加或删除，而不会产生任何数据不一致。

非阻塞列表提供这些操作：如果操作不能立即完成（比如，你想要获取列表的元素而列表却是空的），它将根据这个操作抛出异常或返回null值。Java 7引进实现了非阻塞并发列表的ConcurrentLinkedDeque类。

[示例](../src/test/java/com/getset/j7cc/chapter6/ConcurrentCollections.java#L)演示了基于ConcurrentLinkedDeque的多线程增删元素。

## 6.3 使用阻塞线程安全列表 

阻塞列表与非阻塞列表的主要区别是，阻塞列表有添加和删除元素的方法，如果由于列表已满或为空而导致这些操作不能立即进行，它们将阻塞调用的线程，直到这些操作可以进行。Java包含实现阻塞列表的LinkedBlockingDeque类。

[示例](../src/test/java/com/getset/j7cc/chapter6/ConcurrentCollections.java#L)演示了基于LinkedBlockingDeque的多线程增删元素。

LinkedBlockingDeque类同时提供方法用于添加和获取列表的元素，而不被阻塞，或抛出异常，或返回null值。这些方法是：

  * takeFirst() 和takeLast()：这些方法分别返回列表的第一个和最后一个元素。它们从列表删除返回的元素。如果列表为空，这些方法将阻塞线程，直到列表有元素。
  * getFirst() 和getLast()：这些方法分别返回列表的第一个和最后一个元素。它们不会从列表删除返回的元素。如果列表为空，这些方法将抛出NoSuchElementExcpetion异常。
  * peek()、peekFirst(),和peekLast()：这些方法分别返回列表的第一个和最后一个元素。它们不会从列表删除返回的元素。如果列表为空，这些方法将返回null值。
  * poll()、pollFirst()和 pollLast()：这些方法分别返回列表的第一个和最后一个元素。它们从列表删除返回的元素。如果列表为空，这些方法将返回null值。
  * dd()、 addFirst()、addLast()：这些方法分别在第一个位置和最后一个位置上添加元素。如果列表已满（你已使用固定大小创建它），这些方法将抛出IllegalStateException异常。

## 6.4 用优先级对使用阻塞线程安全列表排序

当你需要使用一个有序列表的数据结构时，Java提供的PriorityBlockingQueue类就拥有这种功能。

你想要添加到PriorityBlockingQueue中的所有元素必须实现Comparable接口。这个接口有一个compareTo()方法，它接收同样类型的对象，你有两个比较的对象：一个是执行这个方法的对象，另一个是作为参数接收的对象。如果本地对象小于参数，则该方法返回小于0的数值。如果本地对象大于参数，则该方法返回大于0的数值。如果本地对象等于参数，则该方法返回等于0的数值。

PriorityBlockingQueue使用compareTo()方法决定插入元素的位置。（校注：默认情况下）较大的元素将被放在队列的尾部。

阻塞数据结构（blocking data structure）是PriorityBlockingQueue的另一个重要特性。它有这样的方法，如果它们不能立即进行它们的操作，则阻塞这个线程直到它们的操作可以进行。

[示例](../src/test/java/com/getset/j7cc/chapter6/ConcurrentCollections.java#L)使用PriorityBlockingQueue类实现，在相同的列表上使用不同的优先级存储大量事件（event），然后检查队列的排序是否是你想要的。

## 6.5 使用线程安全与带有延迟元素的列表 

DelayedQueue类是Java API提供的一种有趣的数据结构，并且你可以用在并发应用程序中。在这个类中，你可以存储带有激活日期的元素。方法返回或抽取队列的元素将忽略未到期的数据元素。它们对这些方法来说是看不见的。

为了获取这种行为，你想要存储到DelayedQueue类中的元素必须实现Delayed接口。这个接口允许你处理延迟对象，所以你将实现存储在DelayedQueue对象的激活日期，这个激活时期将作为对象的剩余时间，直到激活日期到来。这个接口强制实现以下两种方法：

  * compareTo(Delayed o)：Delayed接口继承Comparable接口。如果执行这个方法的对象的延期小于作为参数传入的对象时，该方法返回一个小于0的值。如果执行这个方法的对象的延期大于作为参数传入的对象时，该方法返回一个大于0的值。如果这两个对象有相同的延期，该方法返回0。
  * getDelay(TimeUnit unit)：该方法返回与此对象相关的剩余延迟时间，以给定的时间单位表示。TimeUnit类是一个枚举类，有以下常量：DAYS、HOURS、 MICROSECONDS、MILLISECONDS、 MINUTES、 NANOSECONDS 和 SECONDS。

[示例](../src/test/java/com/getset/j7cc/chapter6/ConcurrentCollections.java#L)使用DelayedQueue类来存储一些具有不同激活日期的事件。

## 6.6 使用线程安全的NavigableMap

Java API 提供的有趣的数据结构，并且你可以在并发应用程序中使用，它就是ConcurrentNavigableMap接口的定义。实现ConcurrentNavigableMap接口的类存储以下两部分元素：

  * 唯一标识元素的key
  * 定义元素的剩余数据
  
每部分在不同的类中实现。

Java API 也提供了这个接口的实现类，这个类是ConcurrentSkipListMap，它实现了非阻塞列表且拥有ConcurrentNavigableMap的行为。在内部实现中，它使用Skip List来存储数据。Skip List是基于并行列表的数据结构，它允许我们获取类似二叉树的效率。使用它，你可以得到一个排序的数据结构，这比排序数列使用更短的访问时间来插入、搜索和删除元素。

当你往map中插入数据时，它使用key来排序它们，所以，所有元素将是有序的。除了返回具体的元素，这个类也提供了获取map的子map的方法。

[示例](../src/test/java/com/getset/j7cc/chapter6/ConcurrentCollections.java#L)使用ConcurrentSkipListMap进行线程安全的元素插入，取出时发现是按key排序的。

## 6.7 生成并行随机数 

Java并发API提供指定的类在并发应用程序中生成伪随机。它是ThreadLocalRandom类，这是Java 7版本中的新类。它使用线程局部变量。每个线程希望以不同的生成器生成随机数，但它们是来自相同类的管理，这对程序员是透明的。在这种机制下，你将获得比使用共享的Random对象为所有线程生成随机数更好的性能。

[示例](../src/test/java/com/getset/j7cc/chapter6/ConcurrentCollections.java#L)使用ThreadLocalRandom生成线程独立的随机数。

## 6.8 使用原子变量

在Java 1.5中就引入了原子变量，它提供对单个变量的原子操作。当你在操作一个普通变量时，你在Java实现的每个操作，在程序编译时会被转换成几个机器能读懂的指令。例如，当你分配一个值给变量，在Java你只使用了一个指令，但是当你编译这个程序时，这个指令就被转换成多个JVM 语言指令。这样子的话当你在操作多个线程且共享一个变量时，就会导致数据不一致的错误。

为了避免这样的问题，Java引入了原子变量。当一个线程正在操作一个原子变量时，即使其他线程也想要操作这个变量，类的实现中含有一个检查那步骤操作是否完成的机制。 基本上，操作获取变量的值，改变本地变量值，然后尝试以新值代替旧值。如果旧值还是一样，那么就改变它。如果不一样，方法再次开始操作。这个操作称为 Compare and Set（校对注：简称CAS，比较并交换的意思）。

原子变量不使用任何锁或者其他同步机制来保护它们的值的访问。他们的全部操作都是基于CAS操作。它保证几个线程可以同时操作一个原子对象也不会出现数据不一致的错误，并且它的性能比使用受同步机制保护的正常变量要好。

[示例](../src/test/java/com/getset/j7cc/chapter6/ConcurrentCollections.java#L)使用AtomicInteger保证多线程的自增操作是原子的，从而线程安全，同时使用普通的Integer做对比。

## 6.9 使用原子阵列 

当你实现一个多个线程共享一个或者多个对象的并发应用时，你就要使用像锁或者同步关键词（例如synchronized）来对他们的属性的访问进行保护，来避免并发造成的数据不一致的错误。

但是这些机制会有以下一些缺点：

死锁(dead lock)：例如：当一个线程等待一个锁的时候，会被阻塞，而这个锁被其他线程占用并且永不释放。这种情况就是死锁，程序在这种情况下永远都不会往下执行。

即使只有一个线程在访问共享对象，它也要执行必要的获取锁和释放锁的代码。

CAS(compare-and-swap)操作为并发操作对象的提供更好的性能，CAS操作通过以下3个步骤来实现对变量值得修改：

  * 获取当前内存中的变量的值
  * 用一个新的临时变量(temporal variable)保存改变后的新值
  * 如果当前内存中的值等于变量的旧值，则将新值赋值到当前变量；否则不进行任何操作

对于这个机制，你不需要使用任何同步机制，这样你就避免了 deadlocks，也获得了更好的性能。这种机制能保证多个并发线程对一个共享变量操作做到最终一致。

Java 在原子类中实现了CAS机制。这些类提供了compareAndSet() 方法；这个方法是CAS操作的实现和其他方法的基础。

Java 中还引入了原子Array，用来实现Integer类型和Long类型数组的操作。

[示例](../src/test/java/com/getset/j7cc/chapter6/ConcurrentCollections.java#L)演示如何使用AtomicIntegerArray 类来操作原子 arrays。