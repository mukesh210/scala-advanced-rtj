package lectures.part5typesystem

/*
  Compile-Time Duck Typing
 */
object StructuralTypes extends App {

  // structural types
  // someone implemented this
  type JavaClosable = java.io.Closeable

  // some other team implemented this
  class HipsterClosable {
    def close(): Unit = println("yeah yeah... i'am  closing")
    def closeSilently(): Unit = println("not making a sound")
  }

  // write a method that accepts both of the above types
  // def closeQuitely(closeable: JavaClosable OR HipsterClosable) // compiler error
  //Solution:
  type UnifiedCloseable = {
    def close(): Unit
  } // STRUCTURAL TYPES

  def closeQuitely(unifiedCloseable: UnifiedCloseable): Unit = unifiedCloseable.close()

  closeQuitely(new JavaClosable {
    override def close(): Unit = println("closing...")
  })

  closeQuitely(new HipsterClosable)



  // TYPE REFINEMENT

  // here AdvancedCloseable is JavaCloseable + closeSilently Method
  type AdvancedCloseable = JavaClosable {
    def closeSilently(): Unit
  }

  class AdvancedJavaCloseable extends JavaClosable {
    override def close(): Unit = println("Java closes")
    def closeSilently(): Unit = println("Java closes silently")
  }

  def closeShh(advancedCloseable: AdvancedCloseable): Unit = advancedCloseable.closeSilently()

  closeShh(new AdvancedJavaCloseable)

  // won't work because compiler is not able to find base class(JavaCloseable)
  // even though class contains both methods
  // closeShh(new HipsterClosable)

  // using structural types as standalone types
  def alternativeClose(closeable: { def close(): Unit }): Unit = closeable.close()

  // structural types at compiler type checking => duck typing
  type SoundMaker = {
    def makeSound(): Unit
  }

  class Dog {
    def makeSound(): Unit = println("Bark...")
  }

  class Car {
    def makeSound(): Unit = println("vroom...")
  }

  val dog: SoundMaker = new Dog
  val car: SoundMaker = new Car // both of these works
  // static duck typing -- right side should abide to the structure of left one

  // CAVEAT: structural types and type enrichment are based on reflections
  // Reflections have performance impact... so use them when it's absolutely necessary

  /*
    Exercises
   */

  trait CBL[+T] {
    def head: T
    def tail: CBL[T]
  }

  class Human {
    def head: Brain = new Brain
  }

  class Brain {
    override def toString: String = "BRAINZ!"
  }

  def f[T](somethingWithHead: { def head: T }): Unit = println(somethingWithHead.head)

  /*
    f is compatible with CBL and with a human? Yes
   */

  case object CBNil extends CBL[Nothing] {
    override def head: Nothing = ???

    override def tail: CBL[Nothing] = ???
  }

  case class CBCons[T](override val head: T, override val tail: CBL[T]) extends CBL[T]

  f(CBCons(2, CBNil))
  f(new Human)  // what is T?? T = Brain

  // 2.
  object HeadEqualizer {
    type Headable[T] = { def head: T }
    def ===[T](a: Headable[T], b: Headable[T]): Boolean = a.head == b.head
  }

  /*
    Is HeadEqualizer compatible with CBL and a Human? Yes
   */

  val brainsList = CBCons(new Brain, CBNil)
  val stringsList = CBCons("Brains", CBNil)

  HeadEqualizer.===(brainsList, new Human)
  // problem:
  HeadEqualizer.===(new Human, stringsList) // not type safe
}
