package playground

object Options extends App {

  val bag = List("1", "2", "foo", "3", "bar")
  val sum = bag.flatMap(toInt).sum
  println(s"sum: ${sum}")

  def toInt(in: String): Option[Int] = {
    try {
      Some(Integer.parseInt(in.trim))
    } catch {
      case e: NumberFormatException => None
    }
  }
}
