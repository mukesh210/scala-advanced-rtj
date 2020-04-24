package lectures.part5typesystem

object RockingInheritance extends App {

  // convenience
  trait Writer[T] {
    def write(value: T): Unit
  }
  trait Closeable {
    def close(status: Int): Unit
  }
  trait GenericStream[T] {
    // some methods
    def foreach(f: T => Unit): Unit
  }

  // when we don't know what should be argument type, we can express it in below format
  // and all methods would be available to us
  def processStream[T](stream: GenericStream[T] with Writer[T] with Closeable): Unit = {
    stream.foreach(println)
    stream.close(0)
  }

  // 2. Diamond problem
  trait Animal { def name: String }
  trait Lion extends Animal { override def name: String = "Lion" }
  trait Tiger extends Animal { override def name: String = "Tiger" }
  class Mutant extends Lion with Tiger

  val m = new Mutant
  println(m.name)
  /*
    Mutant extends Animal with { override def name: String = "Lion" }
      extends Animal with { override def name: String = "Tiger" }

    LAST OVERRIDE GETS PICKED -- this is how SCALA resolved Diamond problem
   */

  // 3. the super problem + type linearization

  trait Cold {
    def print = println("cold")
  }
  trait Green extends Cold {
    override def print: Unit = {
      println("green")
      super.print
    }
  }
  trait Blue extends Cold {
    override def print: Unit = {
      println("blue")
      super.print
    }
  }
  class Red {
    def print: Unit = println("red")
  }

  class White extends Red with Green with Blue {
    override def print: Unit = {
      println("white")
      super.print
    }
  }

  println("----------------------")
  val color = new White
  color.print

  /*
    white extends Red extends Cold with "green"
      extends Cold with "blue"

    while extends "cold" with "print green"
   */


}
