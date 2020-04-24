package lectures.part3concurrency

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference

import scala.collection.parallel.ForkJoinTaskSupport
import scala.collection.parallel.immutable.ParVector

object ParallelUtils extends App {

  // 1. Parallel collections

  val parList = List(1, 2, 3).par
  // or
  val aParVector = ParVector[Int](1, 2, 3)

  /*
    Seq
    Vector
    Array
    Map - Hash, Trie
    Set - Hash, Trie
    etc.
   */

  def measure[T](operation: => T): Long = {
    val startTime = System.currentTimeMillis()
    operation
    val endTime = System.currentTimeMillis()
    endTime - startTime
  }

  val smallList = (1 to 10000).toList
  val serialTime = measure {
    smallList.map(_ + 1)
  }
  println(s"SmallList Serial Time: ${serialTime}")
  val parallelTime = measure {
    smallList.par.map(_ + 1)
  }
  println(s"SmallList Parallel Time: ${parallelTime}")

  val largeList = (1 to 10000000).toList
  val largeListSerialTime = measure {
    largeList.map(_ + 1)
  }
  println(s"LargeList Serial Time: ${largeListSerialTime}")
  val largeListParallelTime = measure {
    largeList.par.map(_ + 1)
  }
  println(s"LargeList Parallel Time: ${largeListParallelTime}")

  /*
    Parallel collections work by Map-reduce model
      - split the elements into chunks - Splitter
      - operation
      - recombine - Combiner
   */

  // Operations defined on Parallel collection: map, flatMap, filter, foreach, reduce, fold

  // be careful with reducer, fold --- operation must be associative
  println(List(1, 2, 3).reduce(_ - _))
  println(List(1, 2, 3).par.reduce(_ - _))

  // synchronization
  // might result in different results since foreach would be getting run on different
  // threads so might run into race conditions
  var sum = 0
  List(1, 2, 3).par.foreach(sum += _)
  println(s"sum: ${sum}")

  // configuring parallel collections
  aParVector.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2))
  /*
    alternatives:
      - ThreadPoolTaskSupport // deprecated
      - ExecutionContextTaskSupport(ExecutionContext)
   */

  // 2. Atomic operations and references
  // atomic operations - all or nothing

  val atomic = new AtomicReference[Int](2)

  val currentValue = atomic.get() // thread-safe read
  atomic.set(4) // thread-safe write

  atomic.getAndSet(5) // thread-safe combo

  // if value = 38 then set it to 56 (compare - referential equality)
  atomic.compareAndSet(38, 56)

  atomic.updateAndGet(_ + 1)  // thread-safe function run

  atomic.getAndUpdate(_ + 1)

  atomic.accumulateAndGet(12, _ + _)  // thread-safe accumulation

  atomic.getAndAccumulate(12, _ + _)

}
