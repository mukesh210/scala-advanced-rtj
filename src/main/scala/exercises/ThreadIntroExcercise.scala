package exercises

object ThreadIntroExcercise extends App {
  /*
  1. construct 50 inception threads
  thread1 -> thread2 -> thread 3
  println("Hello from thread no.")

  print in reverse order
   */

  // Excercise 1:

  def inceptionThreads(maxThreads: Int, currentIndex: Int): Thread = {
    new Thread(() => {
      if(currentIndex <= maxThreads) {
        val newThread = inceptionThreads(maxThreads, currentIndex + 1)
        newThread.start()
        newThread.join()  // wait for thread to finish

        println(s"Hello from Thread ${currentIndex}")
      }
    })
  }

  inceptionThreads(50, 1).start()


  // Exercise 2
  var x = 0
  val threads = (1 to 100).map(_ => new Thread(() => x += 1))
  threads.foreach(_.start())
  threads.foreach(_.join())
  println(s"value of x is: ${x}")
  // biggest possible value for x: 100 (when all threads execute sequentially)
  // smallest possible value for x: 1 (when all threads start at same time and read same value for x)

  // Exercise 3
  // Sleep fallacy
  var message = ""
  val awesomeThread = new Thread(() => {
    Thread.sleep(1000)
    message = "Scala is awesome"
  })

  message = "Scala sucks"
  awesomeThread.start()
  Thread.sleep(1001)
  awesomeThread.join()
  println(s"message: ${message}")
  /*
  Question: What's the value of message? -> almost always "Scala is awesome"
  IS it guaranteed?
  why? why not?

  (main thread)
    message = "Scala sucks"
    awesomeThread.start()
    sleep() - relieves execution
   (awesome thread)
    sleep() - relieves execution

   (OS gives the CPU to some important thread - takes CPU for more than 2 seconds)
   (OS gives the CPU back to the MAIN thread)
    println("Scala sucks")
   (OS gives the CPU to awesomethread)
    message = "Scala is awesome"

  Sleeping does not guarantee order of execution of codes and we should not use it to order execution

  How to fix this??
  // Synchronizing?? -- No
  // Use JOIN - works, wait for awesomeThread to finish before printing message
   */
}
