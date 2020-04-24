package lectures.part4implicits

object ImplicitsIntro extends App {

  val pair: (String, String) = "value" -> "210"
  val intPair = 1 -> 2

  case class Person(name: String) {
    def greet = s"Hi, my name is ${name}"
  }

  implicit def fromStringToPerson(str: String) = Person(str)

  // 'greet' method is not defined on String class, but compiler won't give error
  // Compiler will try to find some implicit conversion which will convert String into
  // something on which 'greet' method is defined.
  // for below code, it will be re-written as
  // println(fromStringToPerson("Mukesh").greet)
  println("Mukesh".greet)

  // if we write below code, compiler would give error since there are multiple
  // implicits which can help in achieving objective
  /*class A {
    def greet: Int = 2
  }

  implicit def fromStringToA(str: String): A = new A*/


  // implicit parameters
  def increment(x: Int)(implicit amount: Int) = x + amount
  implicit val defaultValue = 10

  println(increment(2))
}
