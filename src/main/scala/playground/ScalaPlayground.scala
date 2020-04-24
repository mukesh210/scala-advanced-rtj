package playground

import scala.collection.SeqView

object ScalaPlayground extends App {
  // lazy
  val list1 = List(1, 2)
  val list2 = list1.map(x => {
    println(s"Processing ${x}")
    x*2
  })
  println(s"List2: ${list2}")

  lazy val list3 = list1.map(x => {
    println(s"Processing ${x}")
    x*2
  })
  println("After defining lazy list3")
  println(s"List3: ${list3}")

  // call by name

  def something = {
    println("Processing happening in something method")
    1
  }

  def callByValue(func: Int) = {
    println("Inside callByValue function")
    func
  }

  def callByName(func: => Int) = {
    println("Inside callByName function")
    func
  }
  println("-------- Call by value ---------")
  println(callByValue(something))

  println("-------- Call by name ---------")
  println(callByName(something))

  val res: SeqView[Int, List[Int]] = list1.view.take(10)
}
