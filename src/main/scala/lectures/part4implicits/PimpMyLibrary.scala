package lectures.part4implicits

import scala.annotation.tailrec

object PimpMyLibrary extends App {

  // 2.isEven
  // implicit class takes a single parameter
  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0

    def sqrt: Double = Math.sqrt(value)

    def times(function: Function0[Unit]) = {
      def nTimes(n: Int): Unit = {
        if(n > 0) {
          function()
          nTimes(n - 1)
        }
      }

      nTimes(value)
    }

    def *[T](list: List[T]) = {
      @tailrec
      def helperFunc(acc: List[T], n: Int): List[T] = {
        if(n == 0) acc
        else {
          helperFunc(acc ++ list, n - 1)
        }
      }

      helperFunc(Nil, value)
    }
  }

  new RichInt(24).isEven
  println(24.isEven)  // this is achieved using implicit class
  // this is called type enrichment = pimping

  // this is how below works
  1 to 10

  import scala.concurrent.duration._
  3.seconds

  // compiler won't do multiple implicit searches

  implicit class RicherInt(richInt: RichInt) {
    def isOdd: Boolean = richInt.value % 2 != 0
  }
  // 42.isOdd -> won't work

  /*
    Enrich the String class
      - asInt
      - encrypt
        John -> Lnjp

    Keep enriching Int class
      - times(function)
        3.times(() => ...)
      - *
        3 * List(1,2) => List(1,2,1,2,1,2)

   */

  implicit class RichString(value: String) {
    def asInt: Int = Integer.parseInt(value)
    def encrypt(distance: Int): String = {
      value.map(c => (c + distance).asInstanceOf[Char])
    }
  }

  println("Rich String")
  println("12".asInt)
  println("John".encrypt(2))

  println(3.times(() => println("Scala Rocks")))
  println(3 * List(1,2))

  // How would you achieve: "3" / 4
  // using implicit methods
  implicit def stringToInt(str: String): Int = Integer.valueOf(str)
  println("4" / 3)  // stringToInt("4")  / 3
  // in above example: Compiler check in below sequence:
  // is there any / operator defined on String --- no
  // is there any implicit class which takes String as input and has / method --- no
  // is there any implicit method which takes String as input and gives a result on which / is defined --- yes

  // equivalent: implicit class RichAltInt(value: Int)
  class RichAltInt(value: Int)
  implicit def enrich(value: Int): RichAltInt = new RichAltInt(value)


  // implicit methods are discouraged: Reason below:
  // danger: very hard to trace bug
  implicit def intToBoolean(i: Int): Boolean = i == 1

  val aConditionValue = if(3) "ok" else "something wrong"
  println(aConditionValue)
}

/*
  Questions:
    1. Can case class be implicit
    2.
 */