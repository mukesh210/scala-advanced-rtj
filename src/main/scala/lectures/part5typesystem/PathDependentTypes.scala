package lectures.part5typesystem

import lectures.part5typesystem.PathDependentTypes.{o, outer}

object PathDependentTypes extends App {

  class Outer {
    class Inner
    object InnerObject
    type InnerType

    def print(i: Inner) = println(i)
    def printGeneral(i: Outer#Inner) = println(i)
  }

  // we can define CLASSES and OBJECTS anywhere we want
  // types are exceptions: they can be defined only inside classes and traits
  // In other places, types can only be aliases
  def aMethod: Int = {
    class HelperClass
    type HelperType = String
    2
  }

  // 2. How to use path dependent types
  // for types nested inside classes and objects, they are per-instance

  // per-instance
  val outer = new Outer
  val inner: outer.Inner = new outer.Inner // outer.Inner is a TYPE

  val o = new Outer
  // val i: o.Inner = new outer.Inner // won't compile --- outer.Inner & o.Inner are different types
  val i: o.Inner = new o.Inner // outer.Inner is a TYPE

  // for accessing Inner types, you need outer instance
  // every outer instance means different inner types(e.g. outer.Inner, o.Inner)

  // o.print(inner)  // won't compile because types are different
  o.print(i)

  // above are called path dependent types

  // NOTE: Inner types have common super types: Outer#Inner
  o.printGeneral(i)
  o.printGeneral(inner) // this works fine

  /*
    Exercise
      DB keyed by Int or String, but maybe others
   */

  trait ItemLike {
    type Key
  }

  trait Item[K] extends ItemLike {
    type Key = K
  }
  trait IntItem extends Item[Int]
  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???


  get[IntItem](42)
  get[StringItem]("home")

  // get[IntItem]("scala")
}
