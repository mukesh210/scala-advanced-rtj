package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {
  /*
    Producer Consumer Problem

    producer -> [ ? ] -> consumer


   */

  class SimpleContainer {
    private var value: Int = 0

    def isEmpty = value == 0

    def get: Int = {
      val result = value
      value = 0
      result
    }

    def set(newValue: Int) = value = newValue
  }

  def naiveProducerConsumer() = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting...")
      while(container.isEmpty){
        println("[consumer] actively waiting...")
      }

      println("[consumer] I have consumer " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] computing...")
      Thread.sleep(500)
      val value = 42
      println(s"[producer] I have produced, after long work, the value: ${value}")
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

  // naiveProducerConsumer()

  // Above approach solves Producer Consumer Problem but here Consumer is always
  // waiting for producer in infinite loop which is not good

  // Another approach: wait and notify --- can only be used in synchronized block
  // only AnyRefs can have Synchronized block(not available on Primitives like Int, etc.)

  def smartProducerConsumer() = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting...")
      container.synchronized {
        container.wait()
      }
      println("waiting ended")
      println(s"[consumer] I have consumed: ${container.get}")
    })

    val producer = new Thread(() => {
      println("[producer] Hard at work")
      Thread.sleep(2000)
      val value = 42

      container.synchronized {
        println(s"[producer] I am producing ${value}")
        container.set(value)
        container.notify()
      }
    })

    consumer.start()
    producer.start()
  }

  // smartProducerConsumer

  /*
    producer -> [ ? ? ? ] -> consumer
   */

  def prodConsLargeBuffer() = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random()

      while(true) {
        buffer.synchronized {
          if(buffer.isEmpty) {
            println("[consumer] buffer empty, waiting...")
            buffer.wait()
          }

          // there must be at least one value to consumer
          val x = buffer.dequeue()
          println(s"[consumer] I consumed ${x}")

          buffer.notify()
        }
        Thread.sleep(random.nextInt(250))
      }

    })

    val producer = new Thread(() => {
      val random = new Random()
      var i = 0

      while(true) {
        buffer.synchronized {
          if(buffer.size == capacity) {
            println("[producer] buffer is full, waiting...")
            buffer.wait()
          }

          // there must be at least one empty space in the buffer
          println(s"[producer] producing ${i}")
          buffer.enqueue(i)

          buffer.notify()

          i += 1
        }

        Thread.sleep(random.nextInt(500))
      }
    })

    consumer.start()
    producer.start()

  }

  // prodConsLargeBuffer()

  /*
    prod-cons, level 3

    producer1 -> [ ? ? ? ] -> consumer1
    producer2 -> [.......] -> consumer2
    producer3 -> [.......] -> consumer3
   */

  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random()

      while(true) {
        buffer.synchronized {
          while(buffer.isEmpty) {
            println(s"[consumer ${id}] buffer empty, waiting...")
            buffer.wait()
          }

          // there must be at least one value to consumer
          val x = buffer.dequeue()
          println(s"[consumer ${id}] I consumed ${x}")

          buffer.notify()
        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread {
    val random = new Random()
    var i = 0

    while (true) {
      buffer.synchronized {
        while (buffer.size == capacity) {
          println(s"[producer ${id}] buffer is full, waiting...")
          buffer.wait()
        }

        // there must be at least one empty space in the buffer
        println(s"[producer ${id}] producing ${i}")
        buffer.enqueue(i)

        buffer.notify()

        i += 1
      }

      Thread.sleep(random.nextInt(250))
    }
  }

  def multiProdCons(nConsumers: Int, nProducers: Int) = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 20

    (1 to nConsumers).foreach(i => new Consumer(i, buffer).start())
    (1 to nProducers).foreach(i => new Producer(i, buffer, capacity).start())
  }

  // multiProdCons(3, 6)

  /*
    Exercises:
    1. think of an example where notifyAll acts in different way than notify?
      A. NotifyAll will wake up all waiting threads and they will acquire lock one by one
    2. create a deadlock
    3. create a livelock
   */

  // Exercise 1
  def testNotifyAll() = {
    val bell = new Object

    (1 to 10).foreach(i => new Thread(() => {
      bell.synchronized {
        println(s"[thread ${i}] waiting")
        bell.wait()
        println(s"[thread ${i}] hurrah!!!")
      }
    }).start())

    new Thread(() => {
      Thread.sleep(2000)
      println("[announcer] Rock n roll")
      bell.synchronized {
        bell.notify()
      }
    }).start()
  }

  // testNotifyAll()

  // Exercise 2 --- deadlock
  case class Friend(name: String) {
    def bow(other: Friend) = {
      this.synchronized {
        println(s"$this: I am bowing to ${other}")
        other.rise(other)
        println(s"${this}: my friend ${other} has risen")
      }
    }

    def rise(other: Friend) = {
      this.synchronized {
        println(s"${this}: I am rising to my friend ${other}")
      }
    }

    var side = "right"
    def switchSide() = {
      if(side == "right") side = "left"
      else side = "right"
    }

    def pass(other: Friend) = {
      while(this.side == other.side) {
        println(s"${this}: oh please, ${other}, feel free to pass...")
        switchSide()
        Thread.sleep(1000)
      }
    }
  }

  val sam = Friend("Sam")
  val pier = Friend("Pier")

//  new Thread(() => sam.bow(pier)).start()
//  new Thread(() => pier.bow(sam)).start()

  // Exercise 3: Livelock
  // no deadlock but they are not able to progress
  new Thread(() => sam.pass(pier)).start()
  new Thread(() => pier.pass(sam)).start()

}
