package lectures.part1as

import scala.util.Try

object DarkSugars extends App {

  // syntax sugar #1: methods with single param
  def singleArgMethod(arg: Int): String = s"$arg little ducks... "

  // can pass parameter as result of code block
  val description = singleArgMethod {
    println("going for returning 42")
    42
  }

  println(s"Description: ${description}")

  // practical example for #1
  val aTryInstance = Try {  // java's try

  }

  // practical example for #1
  List(1, 2, 3).map { x =>
    x + 1
  }

  // syntax sugar #2: single abstract method: Instances of Traits with single methods can be turned into lambda
  trait Action {
    def act(x: Int): Int
  }

  val actionInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }

  val aFunkyInstance: Action = (x: Int) => x + 1

  // example
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("hello, Scala")
  })

  val aFuncThread = new Thread(() => println("sweet, Scala"))

  // Instances of Traits with single abstract method can be turned into LAMBDA
  abstract class AnAbstractType {
    def implemented: Int = 23
    def f(a: Int): Unit
  }

  val anAbstractInstance: AnAbstractType = (x: Int) => println("sweet")

  // syntax sugar #3: the :: and #:: methods

  val prependedList = 2 :: List(3, 4)
  // 2.::List(3, 4)                   // No method :: on Ints

  // List(3, 4) :: 2

  // Scala Spec: last character decides associativity of method

  1 :: 2 :: 3 :: List(4, 5)   // equivalent to
  List(4, 5).::(3).::(2).::(1)

  class MyStream[T] {
    def -->:(value: T): MyStream[T] = this
  }

  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]

  // syntax sugar #4: multi-word method naming

  class Teengirl(name: String) {
    def `and then said`(gossip: String) = println(s"${name} said ${gossip}")
  }

  val lily = new Teengirl("Lily")
  lily `and then said` "Scala is really AWESOME!"

  // syntax sugar #5: infix types
  class Composite[A, B]
  val aComposite: Composite[Int, String] = ??? // is equivalent to
  val bComposite: Int Composite String = ???

  class -->[A, B]
  val towards: Int --> String = ???

  // syntax sugar #6: update() is very special, much like apply()

  val anArray = Array(1, 2, 3)
  anArray(2) = 7      // rewritten to anArray.update(2, 7)
  // used in mutable collection

  // syntax sugar #7: setters for mutable containers

  class Mutable {
    private var internalMember: Int = 0 // private for OO Encapsulation

    def member = internalMember
    def member_=(value: Int) = {
      internalMember = value
    }
  }

  val aMutableContainer = new Mutable
  aMutableContainer.member = 24 // rewritten to aMutableContainer.member_=(24)
}
