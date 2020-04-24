package exercises

import scala.annotation.tailrec

abstract class MyStream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]

  def #::[B >: A](element: B):MyStream[B] // prepend operation
  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] // concat 2 streams

  def foreach(f: A => Unit): Unit
  def map[B](f: A => B): MyStream[B]
  def flatMap[B](f: A => MyStream[B]): MyStream[B]
  def filter(f: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A] // finite stream
  def takeAsList(n: Int): List[A] = take(n).toList()

  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] =
    if(isEmpty) acc
    else tail.toList(head :: acc)

}

object EmptyStream extends MyStream[Nothing] {
  override def isEmpty: Boolean = true

  override def head: Nothing = throw new NoSuchElementException

  override def tail: MyStream[Nothing] = throw new NoSuchElementException

  override def #::[B >: Nothing](element: B): MyStream[B] = new Cons(element, this)

  override def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

  override def foreach(f: Nothing => Unit): Unit = ()

  override def map[B](f: Nothing => B): MyStream[B] = this

  override def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this

  override def filter(f: Nothing => Boolean): MyStream[Nothing] = this

  override def take(n: Int): MyStream[Nothing] = this
}

class Cons[+A](hd: A, tl: => MyStream[A]) extends MyStream[A] {
  override def isEmpty: Boolean = false

  override val head: A = hd
  override lazy val tail: MyStream[A] = tl // call by need

  override def #::[B >: A](element: B): MyStream[B] = new Cons(element, this)

  override def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new Cons(head, tail ++ anotherStream)

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  override def map[B](f: A => B): MyStream[B] = {
    new Cons(f(head), tail.map(f)) // preserves lazy evaluation
  }

  override def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)

  override def filter(f: A => Boolean): MyStream[A] = {
    if(f(head)) new Cons(head, tail.filter(f))
    else tail.filter(f) // will preserve lazy evaluation
  }

  override def take(n: Int): MyStream[A] =
    if(n <= 0) EmptyStream
    else if(n == 1) new Cons(head, EmptyStream)
    else new Cons(head, tail.take(n - 1))
}

object MyStream {
  def from[A](start: A)(generator: A => A): MyStream[A] =
    new Cons(start, MyStream.from(generator(start))(generator))
}

object StreamsPlayground extends App {
  val naturals = MyStream.from(1)(_ + 1)
  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)

  val startFromZero = 0 #:: naturals  // naturals.#::(0)
  println(startFromZero.head)

  startFromZero.take(10000).foreach(println)

  // map, flatMap
  println(startFromZero.map(_ * 2).take(10).toList())
  println(startFromZero.flatMap(x => new Cons(x, new Cons(x + 1, EmptyStream))).take(10).toList())
  println(startFromZero.filter(_ < 10).take(10).take(20).toList())

  // Exercises on Streams
  // 1 - stream of fibonacci numbers
  // 2 - stream of prime number with Eratosthenes sieve

  // 1st exercise
  // 1, 1, 2, 3, 5, 8, 13
  def fiboncci(first: BigInt, second: BigInt): MyStream[BigInt] =
    new Cons[BigInt](first, fiboncci(second, first + second))

  println(fiboncci(1, 1).take(10).toList())

  /*
  [ 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 ...
  [ 2 3 5 7 9 11 13 15 ...
  [ 2 erathos applied to (numbers filtered by n % 2 != 0)
  [ 2 3 erathos applied to [ 5 7 9 11... ] filtered by n % 3 != 0
  [ 2 3 5

   erathosthenes sieve
   */
  def erathosthenes(numbers: MyStream[Int]): MyStream[Int] =
    if(numbers.isEmpty) numbers
    else new Cons(numbers.head, erathosthenes(numbers.tail.filter(_ % numbers.head != 0)))

  println(erathosthenes(MyStream.from(2)(_ + 1)).take(10).toList())
}
