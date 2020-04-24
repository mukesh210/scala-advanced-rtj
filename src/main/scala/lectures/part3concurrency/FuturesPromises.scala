package lectures.part3concurrency

import scala.annotation.tailrec
import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Random, Success, Try}
import scala.concurrent.duration._

object FuturesPromises extends App {

  def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture: Future[Int] = Future {
    calculateMeaningOfLife  // spawns thread to run this
  }

  println(aFuture.value)
  println("waiting on the future")

  // onComplete is used for side-effects(it's return type is Unit)
  aFuture.onComplete {
    case Success(value) => println(s"meaning of life is: ${value}")
    case Failure(exception) => println(s"I have failed with ${exception}")
  }
  /*
    onComplete callback maybe called by one of the thread
      the thread executing the future
      a new thread
     there is no way to know which thread is executing callback
     DON'T ASSUME ANYTHING IN PARALLEL PROGRAMMING
   */

  Thread.sleep(2000)

  // mini social network

  case class Profile(id: String,  name: String) {
    def poke(anotherProfile: Profile) = {
      println(s"${this.name} poking ${anotherProfile.name}")
    }
  }

  object SocialNetwork {
    // "database" of profiles
    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.0-dummy" -> "Dummy"
    )

    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )

    val random = new Random()

    // API
    def fetchProfile(id: String): Future[Profile] = Future {
      Thread.sleep(random.nextInt(500))
      Profile(id, names(id))
    }

    def fetchBestFriends(profile: Profile) = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }
  }

  // client: mark to pill bill
  val mark = SocialNetwork.fetchProfile( "fb.id.1-zuck")
//  mark.onComplete {
//    case Success(markProfile) => {
//      val bill = SocialNetwork.fetchBestFriends(markProfile)
//      bill.onComplete {
//        case Success(billProfile) => markProfile.poke(billProfile)
//        case Failure(e) => e.printStackTrace()
//      }
//    }
//    case Failure(e) => e.printStackTrace()
//  }



  /*
    above approach of nested onComplete callback is not good
    With this, we can't really use bill object outside and also code looks ugly
   */

    // solution to above problem: Functional composition of futures
    // map, flatMap, filter

  val nameOfTheWall: Future[String] = mark.map(profile => profile.name)
  val marksBestFriend: Future[Profile] = mark.flatMap(profile => SocialNetwork.fetchBestFriends(profile))
  val zucksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("z"))

  // for-comprehension
  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriends(mark)
  } mark.poke(bill)

  Thread.sleep(1000)

  // fallbacks
  val aProfileNoMatterWhat: Future[Profile] = SocialNetwork
    .fetchProfile("unknown id")
    .recover {
      case e: Throwable => Profile("fb.id.0-dummy", "forever alone")
    }

  val aFetchedProfileNoMatterWhat: Future[Profile] = SocialNetwork
    .fetchProfile("unknown id")
    .recoverWith {  // return future
      case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
    }

    // in case of error from second call, error of first will be sent as result
  val fallbackResult = SocialNetwork
    .fetchProfile("unknown id")
    .fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))

  // https://stackoverflow.com/questions/25946942/what-is-the-use-of-scala-future-fallbackto
  // code passed to fallbackTo is not evaluated lazily, so it will be started when first future is started
  // for avoiding fallback future to execute, use recoverWith

  // online banking app
  case class User(name: String)
  case class Transaction(
                          sender: String,
                          receiver: String,
                          amount: Double,
                          status: String
                        )

  object BankingApp {
    val name = "RockTheJVM Banking"

    def fetchUser(name: String): Future[User] = Future {
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
      // simulate some processes
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "Success")
    }

    def purchase(username: String, item: String, merchantName: String, cost: Double): String = {
      // fetch the user from the db
      // create a transaction from usernamr to merchant name
      // WAIT for transaction to finish

      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      // block until future is ready
      Await.result(transactionStatusFuture, 2.seconds)  // implicit conversion -> pimp my library
    }
  }

  println(BankingApp.purchase("Daniel", "iPhone 12", "rock the JVM store", 3000))

  // futures can only be read... to control them explicitly we use Promises
  // promises
  // below we are going to solve Producer Consumer problem using Futures and Promises

  val promise = Promise[Int]()  // "controller" over a future
  val future = promise.future

  // Thread 1 - consumer
  future.onComplete {
    case Success(r) => println(s"[consumer] I have received: ${r}")
  }

  // Thread 2 - Producer
  val producer = new Thread(() => {
    println("[producer] crunching numbers")
    Thread.sleep(500)
    // "fulfulling" the promise
    promise.success(42)
    println("[producer] produced but going to sleep")
    Thread.sleep(4000)
    println("[producer] Done!")
  })

  //producer.start()

  /*
    1. fulfill a future immediately with a value
    2. inSequence(fa, fb)
    3. first(fa, fb) => new future with the first value of 2 futures
    4. last(fa, fb) => new future with the last value
    5. retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T]
   */

  // Exercise 1 solution:
  val immediateFuture: Future[Int] = Future {
    42
  }

  immediateFuture.onComplete {
    case Success(value) => println(s"Immediate Future resolved: ${value}")
  }
  Thread.sleep(3000)
  // Exercise 2 solution:
  def inSequence[A, B](firstFuture: => Future[A], secondFuture: => Future[B]) = {
    for {
      firstValue <- firstFuture
      secondValue <- secondFuture
    } println(s"firstValue: ${firstValue} secondValue: ${secondValue}")
  }

  def firstFuture = Future {
    println("first future initializing...")
    Thread.sleep(2000)
    println("first future returning 123")
    123
  }

  def secondFuture = Future {
    println("second future initializing...")
    Thread.sleep(500)
    println("second future returning 456")
    456
  }
  inSequence(firstFuture, secondFuture)
  Thread.sleep(3000)

  // Exercise 3 solution:
  def first[A](fa: Future[A], fb: Future[A]): Future[A] = {
    val promise = Promise[A]
    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)

    promise.future
  }

  // Exercise 4 solution:
  def last[A](fa: Future[A], fb: Future[A]): Future[A] = {
    val bothPromise = Promise[A]
    val lastPromise = Promise[A]
    val checkAndComplete = (result: Try[A]) =>
      if(!bothPromise.tryComplete(result))
        lastPromise.complete(result)

    fa.onComplete(checkAndComplete)
    fb.onComplete(checkAndComplete)

    lastPromise.future
  }

  val first = Future {
    Thread.sleep(100)
    42
  }

  val second = Future {
    Thread.sleep(200)
    45
  }

  println("Exercise 4 solution:")
  first(first, second).foreach(println)
  last(first, second).foreach(println)
  Thread.sleep(2000)

  println("EXERCISE 5 SOLUTION -----------------------")
  // Exercise 5 solution:
  def retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T] = {
    action()
        .filter(condition)
        .recoverWith {
          case _ => retryUntil(action, condition)
        }

    // my implementation
//    action().flatMap(value => {
//      if(condition(value))
//        Future(value)
//      else retryUntil(action, condition)
//    })
  }

  def randomNumberGeneratorFuture(): Future[Int] = Future {
    println("Going for generating random number...")
    val random = new Random()
    Thread.sleep(100)
    val result = random.nextInt(100)
    println(s"Generated: ${result}")
    result
  }

  retryUntil(randomNumberGeneratorFuture, (x: Int) => x % 3 == 0).foreach(result => println(s"settled at ${result}"))

  Thread.sleep(4000)
}
