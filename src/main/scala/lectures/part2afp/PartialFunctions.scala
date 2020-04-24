package lectures.part2afp

object PartialFunctions extends App {

  // define a function which accepts only 1, 2 and 5 as input

  class NotFoundException extends RuntimeException
  val fussyImpl = (x: Int) =>
    if(x == 1) 42
    else if(x == 2) 56
    else if(x == 5) 78
    else throw new NotFoundException

  // total function: Function applicable on entire domain
  val nicerFussyImpl = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 78
    case _ => throw new NotFoundException
  }

  // Partial function: Function applicable only on some values: Only defined for subset of given domain
  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 78
  } // partial function value

  println(s"aPartialFunction: ${aPartialFunction(2)}")
  // println(s"aPartialFunction 1: ${aPartialFunction(254)}")

  // PF utilities
  println(s"isDefinedAt 254: ${aPartialFunction.isDefinedAt(254)}")

  // lift: convert partial function to total function
  val lifted: Int => Option[Int] = aPartialFunction.lift
  println(s"lifted 2: ${lifted(2)}")
  println(s"lifted 254: ${lifted(254)}")

  val pfChain = aPartialFunction.orElse[Int, Int]{
    case 45 => 67
  }

  println(s"pfChain 2: ${pfChain(2)}")
  println(s"pfChain 45: ${pfChain(45)}")

  // PF extends Total/Normal functions

  // we can pass Partial function to Normal Function
  val aTotalFunction: Int => Int = {
    case 1 => 42
  }

  // HOFs accept Partial Functions as well
  val aMappedList = List(1, 2, 3).map {
    case 1 => 42
    case 2 => 56
    case 3 => 78
  }

  println(s"aMappedList: ${aMappedList}")

  /*
  Note: Partial Functions can only have ONE parameter type
   */

  /*
  1. construct a PF instance yourself(anonymous class)
  2. dumb chatbot as a PF
   */

  val ex1 = new PartialFunction[Int, Int] {
    override def isDefinedAt(x: Int): Boolean = x == 1 || x == 2 || x == 5

    override def apply(v1: Int): Int = v1 match {
      case 1 => 42
      case 2 => 56
      case 5 => 78
    }
  }

  println(s"ex1: ${ex1.isDefinedAt(20)}")

  // dumb chatbot
  val chatbot: PartialFunction[String, String] = {
    case "hi" => "hello"
    case "who are you" => "i am a bot"
  }

  scala.io.Source.stdin.getLines().map(chatbot).foreach(println)
}
