package lectures.part2afp

object CurriesPAF extends App {

  // curried function
  val superAdder: Int => Int => Int = x => y => x + y


  val add3 = superAdder(3)
  println(add3(5))
  println(superAdder(3)(5)) // curried function

  /*
  def's are methods... in cases when expectation is FUNCTION VALUE and if you pass method like in below example

  val list = List(1,2,3,4)
  def inc(x: Int): Int = x + 1
  list.map(inc)                       // ETA-EXPANSION

  method would be converted to FUNCTION VALUE using ETA-EXPANSION
   */


  def curriedAdder(x: Int)(y: Int): Int = x + y

  val add4: Int => Int = curriedAdder(4)
  // lifting = ETA-EXPANSION

  // Partial function applications
  val add5 = curriedAdder(5)_


  // EXERCISES
  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int): Int = x + y
  def curriedAddMethod(x: Int)(y: Int) = x + y

  // create as many possible versions of add7

  // My Solution
//  val impl1: Int => Int = simpleAddFunction(7, _)
//  println(s"Add 5 to 7: ${impl1(5)}")
//
//  val impl2: Int => Int = simpleAddMethod(7, _)
//  println(s"Add 5 to 7: ${impl2(5)}")
//
//  val impl3 = curriedAddMethod(7)_
//  println(s"Add 5 to 7: ${impl3(5)}")
//
//  val impl4: Int => Int = curriedAddMethod(7)
//  println(s"Add 5 to 7: ${impl4(5)}")

  // Tutorial's solution
  val add7 = (x: Int) => simpleAddFunction(7, x)
  val add7_2 = simpleAddFunction.curried(7)
  val add7_6 = simpleAddFunction(7, _: Int)

  val add7_3 = curriedAddMethod(7) _ // _ will force compiler to do ETA Expansion
  val add7_4 = curriedAddMethod(7)(_)

  val add7_5 = simpleAddMethod(7, _: Int) // alternative syntax for turning methods into function values
               // y => simpleAddMethod(7, y)

  // underscores are powerful
  def concatenator(a: String, b: String, c: String) = a + b + c
  val insertName = concatenator("Hello, I am ", _: String, ". How are you?")
  // equivalent to (x) => concatenator("Hello", x, "How are you")
  println(insertName("Mukesh"))

  val fillInTheBlanks = concatenator("Hello, ", _: String, _: String) // (x, y) => concatenator("Hello", x, y)
  println(fillInTheBlanks("Mukesh", "Ajay"))

  // Exercises
  // 1. process list of numbers and return their string representation with different formats
  def curriedFormatter(formatter: String)(num: Double) = formatter.format(num)

  val formatter1 = curriedFormatter("%4.2f")_
  val formatter2 = curriedFormatter("%8.6f")_
  val formatter3 = curriedFormatter("%14.12f")_

  val listOfNums = List(Math.PI, Math.E, 3454.5656756)

  println(s"original list: ${listOfNums}")
  println(s"formatter1: ${listOfNums.map(formatter1)}")
  println(s"formatter2: ${listOfNums.map(formatter2)}")
  println(s"formatter3: ${listOfNums.map(formatter3)}")

  println(s"formatter3: ${listOfNums.map(curriedFormatter("%14.12f"))}") // compiler automatically does the eta expansion

  // 2. difference between
  //  - functions vs methods
  //  - parameters: by-name vs 0-lambda

  def byName(n: => Int): Int = n + 1
  def byFunction(f: () => Int): Int= f() + 1

  def method: Int = 42
  def parenMethod(): Int = 42

  byName(23) // works
  byName(method) // works
  byName(parenMethod()) // works
  byName(parenMethod) // works but beware, equivalent to byName(parenMethod())
  // byName(() => 42)  // doesn't work
  byName((() => 42)())  // works
  // byName(parenMethod _) // doesn't work

  // byFunction(42) // doesn't work
  // byFunction(method) // doesn't work, compiler doesn't do ETA expansion
  byFunction(parenMethod) // method need to be converted to function value, so compiler does ETA expansion
  byFunction(() => 46) // works
  byFunction(parenMethod _) // works but warning, _ unnecessary


}
