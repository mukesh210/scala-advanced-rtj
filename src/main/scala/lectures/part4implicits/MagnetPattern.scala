package lectures.part4implicits

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object MagnetPattern extends App {

  // method overloading
  class P2PRequest
  class P2PResponse
  class Serializer[T]

  trait Actor {
    def receive(statusCode: Int): Int
    def receive(request: P2PRequest): Int
    def receive(request: P2PResponse): Int
    def receive[T: Serializer](message: T): Int
    def receive[T: Serializer](message: T, statusCode: Int): Int
    def receive(future: Future[P2PRequest]): Int
    // def receive(future: Future[P2PResponse]): Int
    // lots of overloads
  }

  /*
    Problems:
      1. TYPE ERASURE
      2. lifting doesn't work for all overloads

      val recieveFV = receive _ // compiler confused

      3. code duplication
      4. type inference and default args
        actor.receive(?!)

      lots of other problems
   */

  // above api can be re-written using TYPE CLASSES

  // below pattern is called MAGNET PATTERN: receive here is acting as center for all activities
  trait MessageMagnet[Result] {
    def apply(): Result
  }

  def receive[R](magnet: MessageMagnet[R]): R = magnet()

  implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int] {
    override def apply(): Int = {
      // logic for handling P2PRequest
      println("Handling P2P Request")
      42
    }
  }

  implicit class FromP2PResponse(response: P2PResponse) extends MessageMagnet[Int] {
    override def apply(): Int = {
      // logic for handling P2PResponse
      println("Handling P2P Response")
      24
    }
  }

  receive(new P2PRequest)
  receive(new P2PResponse)

  /*
    Benefits of MAGNET PATTERN:
   */
  // 1. no more type erasure problems!
  implicit class FromResponseFuture(future: Future[P2PResponse]) extends MessageMagnet[Int] {
    override def apply(): Int = 2
  }

  implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int] {
    override def apply(): Int = 3
  }

  //Note: this works because Compiler looks for implicit resolution before type erasure
  println(receive(Future(new P2PRequest)))
  println(receive(Future(new P2PResponse)))

  // 2 - lifting works
  trait MathLib {
    def add1(x: Int) = x + 1
    def add1(s: String) = s.toInt + 1
    // add1 overloads
  }

  // "magnetise that"
  trait AddMagnet {
    def apply(): Int
  }

  def add1(magnet: AddMagnet): Int = magnet()

  implicit class AddInt(x: Int) extends AddMagnet {
    override def apply(): Int = x + 1
  }

  implicit class AddString(x: String) extends AddMagnet {
    override def apply(): Int = x.toInt + 1
  }

  val addFV = add1 _  // catch: We have not mentioned TYPE PARAMETER in AddMagnet Trait
  println(addFV(1))
  println(addFV("3"))

  // compiler won't be able to lift because it doesn;t know the result type
//  val receiveFV = receive _
//  receiveFV(new P2PResponse)

  /*
    Drawbacks
    1. more verbose
    2. harder to read
    3. you can't name or place default arguments
      receive() won't work
    4. call by name doesn't work correctly
      // exercise - prove it (hint: Side effects)
   */

  class Handler {
    def handle(s: => String) = {
      println(s)
      println(s)
    }
    // other overloads
  }

  trait HandleMagnet {
    def apply(): Unit
  }

  def handle(magnet: HandleMagnet) = magnet()

  implicit class StringToHandle(s: => String) extends HandleMagnet {
    override def apply(): Unit = {
      println(s)
      println(s)
    }
  }

  def sideEffectMethod(): String = {
    println("Hello, Scala")
    "hahaha"
  }

  // handle(sideEffectMethod())

  // CAREFUL: super hard to trace bug
  handle {
    println("Hello, Scala")
    "hahaha"  // new StringHandle("hahaha")
  }
}
