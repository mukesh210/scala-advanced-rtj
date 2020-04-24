package lectures.part4implicits

import java.util.Optional
import java.{util => ju}

object ScalaJavaConversions extends App {

  import collection.JavaConverters._

  val javaSet: ju.Set[Int] = new ju.HashSet[Int]()
  (1 to 5).foreach(javaSet.add)
  println(s"javaset: ${javaSet}")

  val scalaSet = javaSet.asScala
  println(s"scalaSet: ${scalaSet}")

  /*
    iterators
    iterables
    ju.List -> scala.mutable.buffer
    ju.set -> scala.mutable.set
    ju.map -> scala.mutable.map
   */

  // scala to java for mutable collections
  import collection.mutable._

  val numbersBuffer= ArrayBuffer[Int](1, 2, 3)
  val juNumbersBuffer = numbersBuffer.asJava

  println(juNumbersBuffer.asScala eq numbersBuffer)

  val numbers = List(1, 2, 3)
  val juNumbers: ju.List[Int] = numbers.asJava
  val backToScala = juNumbers.asScala

  println(backToScala eq numbers) // false
  println(backToScala == numbers) // true

  // juNumbers.add(7) --- would give error because addition to immutable list is not supported

  /*
    Exercise:
      create a Scala to Java Optional - Option
   */

  class ToScala[T](value: => T) {
    def asScala: T = value
  }

  implicit def asScalaOptional[T](value: ju.Optional[T]): ToScala[Option[T]] = {
    new ToScala[Option[T]](
      if(value.isPresent) Some(value.get) else None
    )
  }

  val juOptional = ju.Optional.of(2)
  val scalaOption = juOptional.asScala
  println(s"scalaOption: ${scalaOption}")

}
