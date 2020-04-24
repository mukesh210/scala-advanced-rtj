package lectures.part2afp

object LazyEvaluation extends App {

  // lazy delays the execution of values till it is required
  lazy val x: Int = throw new IllegalArgumentException

  lazy val y: Int = {
    println("Evaluating y...")
    42
  }
  println("Going for printing y")
  println(y)

  // Examples of Implications
  // 1. side effects
  def sideEffectCondition: Boolean = {
    println("Inside side Effect Condition")
    true
  }

  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition
  println(if(simpleCondition && lazyCondition) "yes" else "no")

  //2. in conjunction with call by name
  def byNameMethod(n: => Int): Int = {
    // CALL BY NEED
    lazy val recurring = n
    recurring + recurring + recurring + 1
  }

  def retrieveMagicNumber(): Int = {
    println("Waiting...")
    Thread.sleep(1000)
    40
  }

  println(byNameMethod(retrieveMagicNumber()))

  // 3. filtering with lazy vals
  def lessThan30(i: Int): Boolean = {
    println(s"${i} is less than 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"${i} is greater than 20?")
    i > 20
  }

  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30) // List(1, 25, 5, 23)
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  val lt30lazy = numbers.withFilter(lessThan30)
  val gt20lazy = lt30lazy.withFilter(greaterThan20)
  println()
  gt20lazy.foreach(println)

  // for comprehension uses withFilter with guards
  for {
    a <- List(1, 2, 3) if a % 2 == 0 // uses lazy vals
  } yield a + 1
  // translated to
  List(1, 2, 3).withFilter(_ % 2 == 0).map(_ + 1) // List[Int]

  /*
  Exercise: Implement a lazily evaluated, singly linked STREAM of elements
  naturals = MyStream.from(1)(x => x + 1) // infinite stream of natural numbers
  naturals.take(100).foreach(println) // lazily evaluated stream of first 100 naturals(finite stream)
  naturals.foreach(println) // will crash- infinite
  naturals.map(_ * 2) // stream of all even numbers (infinite)
   */

}
