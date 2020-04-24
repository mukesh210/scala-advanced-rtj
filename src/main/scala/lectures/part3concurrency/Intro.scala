package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App {
  // JVM threads
  val runnable = new Runnable {
    override def run(): Unit = println("Running in Parallel")
  }
  val aThread = new Thread(runnable)

  // starts a JVM thread which runs on top of OS thread
  aThread.start() // gives signal to JVM to spawn a thread to run

  runnable.run()  // doesn't do anything in parallel

  // joining a thread
  aThread.join()  // blocks until aThread finishes running

  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("Hello")))
  val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("Good bye")))
//  threadHello.start()
//  threadGoodbye.start()

  // different runs produce different results!

  // executors
  val pool = Executors.newFixedThreadPool(10)
//  pool.execute(() => println("something in the thread pool"))

  pool.execute(() => {
    Thread.sleep(1000)
//    println("Done after 1 second")
  })

//  pool.execute(() => {
//    Thread.sleep(1000)
//    println("almost done")
//    Thread.sleep(1000)
//    println("done after 2 seconds")
//  })

  pool.shutdown() // means pool does not take any more executions
  // pool.execute(() => println("should not appear")) // throws an exception in calling thread

  // pool.shutdownNow()
  println(pool.isShutdown)

  // race conditions -> 2 or more threads trying to change shared memory at same time
  def runInParallel = {
    var x = 0

    val thread1 = new Thread(() => {
      x = 1
    })

    val thread2 = new Thread(() => {
      x = 2
    })

    thread1.start()
    thread2.start()
//    println(x)
  }

  //for(_ <- 1 to 10000) runInParallel

  class BankAccount(var amount: Int) {
    override def toString: String = "" + amount
  }

  def buy(account: BankAccount, thing: String, price: Int) = {
    account.amount -= price
//    println(s"I bought ${thing}")
//    println(s"My account is now: ${account}")
  }

//  for(_ <- 1 to 100) {
//    val account = new BankAccount(50000)
//    val thread1 = new Thread(() => buy(account, "shoes", 3000))
//    val thread2 = new Thread(() => buy(account, "iPhone", 4000))
//    thread1.start()
//    thread2.start()
//    Thread.sleep(10)
//    if(account.amount != 43000) println(s"AHA: ${account.amount}")
//  }

  // this is the reason why race conditions are not good and leads to incorrect data

  // Solutions to above problems of race condition:
  // Option #1: use synchronized() block
  def buySafe(account: BankAccount, thing: String, price: Int) =
    account.synchronized {
      // no two threads can evaluate synchronized block at same time
      account.amount -= price
      println(s"I've bought ${thing}")
      println(s"my account is now: ${account}")
    }

  // Option #2: use @volatile
  // this way, the read and write operation on variable will be synchronized

  /*
    Exercises Present in Exercise/ThreadIntroExercise.scala

    1.
   */

}
