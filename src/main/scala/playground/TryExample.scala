package playground

import scala.io.Source
import scala.util.{Failure, Success, Try}

object TryExample extends App {

  val fileName = "/etc/passwd"
  readFile(fileName) match {
    case Success(value) => value.foreach(println)
    case Failure(exception) => println(exception)
  }

  def readFile(fileName: String): Try[List[String]] = {
    Try(Source.fromFile(fileName).getLines().toList)
  }

}
