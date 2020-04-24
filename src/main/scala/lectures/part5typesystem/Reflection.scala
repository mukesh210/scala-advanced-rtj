package lectures.part5typesystem

object Reflection extends App {

  // reflection + macros + quasiquotes => METAPROGRAMMING

  case class Person(name: String) {
    def sayMyName() = println(s"Hi, My name is ${name}")
  }

  // 0 - import
  import scala.reflect.runtime.{universe => ru}

  // 1 - instantiate MIRROR
  val m = ru.runtimeMirror(getClass.getClassLoader)

  // 2 - create class object  = "description"
  val clazz = m.staticClass("lectures.part5typesystem.Reflection.Person") // creating a class object by name

  // 3 - create a reflected mirror = "can Do things on class"
  val cm = m.reflectClass(clazz)

  // 4 - get the constructor
  val constructor = clazz.primaryConstructor.asMethod

  // 5 - reflect the constructor
  val constructorMirror = cm.reflectConstructor(constructor)

  // 6 - invoke the constructor
  val instance = constructorMirror.apply("John")

  println(instance)

  // I have an instance
  val p = Person("Mary")  // from wire as a serialized object
  // method name computed from somewhere else
  val methodName = "sayMyName"

  // 1 - obtain mirror
  // 2 - reflect the instance
  val reflected = m.reflect(p)
  // 3- method symbol
  val methodSymbol = ru.typeOf[Person].decl(ru.TermName(methodName)).asMethod
  // 4 - reflect the method = Can DO things
  val method = reflected.reflectMethod(methodSymbol)
  // 5- invoke the method
  method.apply()

  // Reflection in terms of type erasure
  // pain point 1: differentiate between types at runtime
  val numbers = List(1,2,3)
  // here... list of strings is matched because compiler erases generic types
  numbers match {
    case listOfStrings: List[String] => println(s"list of strings: ${listOfStrings}")
    case listOfInts: List[Int] => println(s"list of ints: ${listOfInts}")
  }

  // PP 2: limitations on overloads
//  def processList(list: List[Int]): Int = 43
//  def processList(list: List[String]): Int = 43

  // TypeTags - reflection for ensuring generic types are available at runtime
  // 0 - import
  import ru._

  // 1 - creating a type tag "manually"
  val ttag: ru.TypeTag[Person] = typeTag[Person]
  println(ttag.tpe)

  class MyMap[K, V]

  // 2- pass typeTags as implicit parameters
  // TypeTags are created at Compile time which has all types information and can be used at RunTime
  def getTypeArguments[T](value: T)(implicit typeTag: TypeTag[T]) = typeTag.tpe match {
    case TypeRef(_, _, typeArguments) => typeArguments
    case _ => List()
  }

  val myMap = new MyMap[Int, String]
  val typeArgs = getTypeArguments(myMap)//(typeTag: TypeTag[MyMap[Int, String]])
  println(s"typeArgs: ${typeArgs}")

  def isSubtype[A, B](implicit ttagA: TypeTag[A], ttagB: TypeTag[B]): Boolean = {
    ttagA.tpe <:< ttagB.tpe
  }

  class Animal
  class Dog extends Animal
  println(isSubtype[Dog, Animal])

  // using TypeTag in above example
  // 3- method symbol
  val anotherMethodSymbol = typeTag[Person].tpe.decl(ru.TermName(methodName)).asMethod
  // 4 - reflect the method = Can DO things
  val sameMethod = reflected.reflectMethod(anotherMethodSymbol)
  // 5- invoke the method
  sameMethod.apply()
}
