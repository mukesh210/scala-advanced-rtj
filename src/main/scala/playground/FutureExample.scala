package playground

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object FutureExample extends App {

  def add(x: Int, y: Int): Future[Int] = Future {
    println("Returning x + y")
    x + y
  }

  def double(n: Int): Future[Int] = Future {
    n * 2
  }

  val res: Future[Int] = add(4, 5)
//  res onComplete  {
//    case Success(value) => println(s"Success: ${value}")
//    case Failure(err) => println(s"Error: ${err}")
//  }
  val res1: Future[Int] = res.flatMap(x => double(x))
  res1.map(x => println(s"Adding and Doubling: ${res1}"))
  println("After calling add function")

  val errorFuture = Future {
    throw new Exception("Mission Failed")
  }
  errorFuture onComplete {
    case Success(value) => println(s"Success: ${value}")
    case Failure(err) => println(s"Error: ${err}")
  }

  // this won't catch exception thrown from ErrorFuture(https://www.credera.com/blog/credera-site/mastering-scala-futures/)
  try {
    errorFuture
  } catch {
    case e => println("------------------------------ caught -----------------------")
  }

  // to catch error occuring in thread... do it like this
  errorFuture recover {
    case e => println("Caught ----------------- Recovered")
  }

  // recoverWith allows us to return another future
  (errorFuture recoverWith {
    case e => add(56, 67)
  }).map(x => println(s"recovered with ${x}"))
  //OR
  errorFuture fallbackTo(add(90, 80)).map(x => println(s"recovered with ${x}"))



  // won't be called because map will proceed only with success
  //errorFuture.map(x => println(s"errorFuture: ${x}"))

  val add1: Future[Int] = add(2, 3)
  val add2: Future[Int] = add(4, 5)
  val add3: Future[Int] = add(6, 7)
  for {result1 <- add1
        result2 <- add2
    result3 <- add3
  } println(s"Results: ${result1} ${result2} ${result3}")

  val aggList: List[Future[Int]] = List(add1, add2, add3)
  val convertedAddList: Future[List[Int]] = Future.sequence(aggList)
  convertedAddList.map(x => println(s"ConvertedAddList: ${x}"))

  def divide(a: Int, b: Int): Future[Int] = Future(a/b)

  val divisionFuture = divide(8, 0)

  def add100(a: Int): Future[Int] = Future(a + 100)

  val additionFuture: Future[Int] = divisionFuture.flatMap(add100)

  additionFuture.foreach(x => println(s"dividing and adding 100 gives: ${x}"))

  Thread.sleep(2000)
  println("End --------------")
}
