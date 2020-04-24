package lectures.part4implicits

object TypeClasses extends App {

  trait HTMLWritable {
    def toHtml: String
  }

//  case class User(name: String, age: Int, email: String) extends HTMLWritable {
//    override def toHtml: String = s"<div>${name} (${age} yo) <a href=${email}/> </div>"
//  }

  case class User(name: String, age: Int, email: String)

  // User("John", 32, "emailid@gmail.com").toHtml

  /*
      1. for the types we write
      2. ONE implementation out of quite a number
   */

  // option 2- pattern matching
  object HTMLSerializable {
    def serializeToHTML(value: Any) = value match {
      case User(n, a, e) =>
      case _ =>
    }
  }

  /*
    1. lost Type safety
    2. need to modify code every time
    3. still ONE implementation
   */

  // solution

  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  implicit object UserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name} (${user.age} yo) <a href=${user.email}/> </div>"
  }

  val john = User("John", 32, "emailid@gmail.com")

  println(UserSerializer.serialize(john))

  // 1. we can define serializers for other types
  import java.util.Date
  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(value: Date): String = s"<div>${value.toString}</div>"
  }

  // 2. we can define multiple serializer
  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(value: User): String = s"<div>${value.name}</div>"
  }

  // HTMLSerializer - TYPE CLASS - class which specifies type of operations on given type parameter T
  // classes/objects extending TYPE CLASSES is called TYPE CLASS INSTANCES
  // it doesn't makes sense to define different instances for them... that is the reason we are defining them as Singleton object


  // part 2 --- using implicits with TYPE CLASSES
  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    def apply[T](implicit serializer: HTMLSerializer[T])= serializer
  }

  implicit object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String = "<div style: color></div>"
  }

  println(HTMLSerializer.serialize(42))
  println(HTMLSerializer.serialize(john))

  // access to entire type class interface
  println(HTMLSerializer[User].serialize((john)))
  // TYPE CLASS


  /*
    LEARNT:
      -- TYPE CLASSES
      -- TYPE CLASSES INSTANCES
      -- HOW TO MAKE COMPILER SEARCH FOR PASSED TYPE CLASS via IMPLICIT using:
          object Equal {
            def apply[T](a: T, b: T)(implicit equalizer: Equal[T]) = equalizer.apply(a, b)
          }
   */

  // part 3 - type enrichment with type classes
  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }

  println(john.toHTML(UserSerializer))  // println(new HTMLEnrichment[User](john).toHTML(UserSerializer))
  println(john.toHTML)  // COOL!

  /*
    - extend to new types
    - different implementation with same type -- chose implementation
    - super expressive
   */

  println(2.toHTML)
  println(john.toHTML(PartialUserSerializer))
  /* Summary:
    - type class itself - HTMLSerializer[T]
    - type class instances(some of which are implicit) - UserSerializer, IntSerializer, etc.
    - conversion with implicit classes - HTMLEnrichment
   */

  // context bounds
  def htmlBoilerplate[T](content: T)(implicit serializer: HTMLSerializer[T]): String = {
    s"<html><body> ${content.toHTML(serializer)} </body></html>"
  }
  // is equivalent to
  // tells compiler to inject implicit parameter of type HTMLSerializer[T]
  // method signature is compact now
  def htmlSugar[T : HTMLSerializer](content: T): String = {
    s"<html><body> ${content.toHTML} </body></html>"
  }

  // implicitly -- use to surface out implicit values so that they can be used elsewhere in code
  case class Permissions(mask: String)
  implicit val defaultPermissions: Permissions = Permissions("0744")

  // in some other part of the code
  val standardPerms = implicitly[Permissions]

  // now htmlSugar can be written as
//  def htmlSugar[T : HTMLSerializer](content: T): String = {
//    val serializer = implicitly[HTMLSerializer[T]]
//    // use serializer to do any operation you want
//    s"<html><body> ${content.toHTML(serializer)} </body></html>"
//  }
}
