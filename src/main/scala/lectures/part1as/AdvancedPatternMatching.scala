package lectures.part1as

object AdvancedPatternMatching extends App {

  val numbers = List(1)
  numbers match {
    case head :: Nil => println(s"only element is ${head}")
    case _ =>
  }

  /*
    constants
    wildcards
    case classes
    tuples
    some special magic like above

    Pattern matching works for 99% of the time but for some case when we need to make our collection compatible
    for pattern matching: do something like below:
   */

  // extractor pattern
  class Person(val name: String, val age: Int)  // Pattern matching won't work because it's not case class

  object Person {
    def unapply(arg: Person): Option[(String, Int)] =
      if(arg.age < 21) None
      else Some(arg.name, arg.age)

    def unapply(age: Int): Option[String] =
      if(age < 21) Some("Minor")
      else Some("Major")
  }

  val bob = new Person("Bob", 25)

  val greeting = bob match {
    case Person(name, age) => s"Hi, My name is $name and my age is $age"
    case _ =>
  }

  println(s"greeting: ${greeting}")

  val legalStatus = bob.age match {
    case Person(status) => status
  }

  println(s"legalStatus: ${legalStatus}")

  // Exercise
  object singleDigit {
    def unapply(arg: Int): Boolean = arg < 10
  }

  object evenNumber {
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }

  object oddNumber {
    def unapply(arg: Int): Boolean = arg % 3 == 0
  }


  val n: Int = 45
  val mathProperty = n match {
    case singleDigit() => "Single Digit"
    case evenNumber() => "Even Number"
    case oddNumber() => "Odd Number"
    case _ => "No Property"
  }

  println("mathProperty:", mathProperty)

  // infix patterns
  case class Or[A, B](a: A, b: B)

  val either = Or(2, "Two")
  val humanDescription = either match {
    // case Or(num, desc) => s"${num} is written as ${desc}"
    case num Or desc => s"${num} is written as ${desc}"
  }

  println(s"HumanDescription: ${humanDescription}")

  // decomposing sequences
  val vararg = numbers match {
    case List(1, _*) => "List starting with 1"
  }

  abstract class MyList[+A] {
    def head: A = ???
    def tail: MyList[A] = ???
  }

  case object Empty extends MyList[Nothing]
  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if(list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  val myListObj: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  val decomposed = myListObj match {
    case MyList(1, 2, _*) => "Starting with 1 and 2"
    case _ => "Something else"
  }

  println(s"Decomposed: ${decomposed}")

  // custom return types for unapply
  // return type of unapply need not be OPTION. It can be anything that implements below 2 methods:
  // isEmpty: Boolean, get

  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper {
    def unapply(arg: Person): Wrapper[String] = new Wrapper[String] {
      override def get: String = arg.name

      override def isEmpty: Boolean = false
    }
  }

  println(bob match {
    case PersonWrapper(name) => s"This person's name is ${name}"
    case _ => "Something else"
  })
}
