package playground

object EitherExample extends App {

  val x: Either[String, Int] = divide(2, 0)

  x match {
    case Left(error: String) => println(error)
    case Right(answer) => println(answer)
  }

  def divide(x: Int, y: Int): Either[String, Int] = {
    if(y == 0) Left("Can't divide by zero")
    else Right(x/y)
  }
}
