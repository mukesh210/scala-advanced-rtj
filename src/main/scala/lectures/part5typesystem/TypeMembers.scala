package lectures.part5typesystem

object TypeMembers extends App {

  class Animal
  class Dog extends Animal
  class Cat extends Animal

  class AnimalCollection {
    type AnimalType // abstract type member
    type BoundedAnimal <: Animal
    type SuperBoundedAnimal >: Dog <: Animal
    type AnimalC = Cat  // Type Alias
  }

  val ac = new AnimalCollection

  // can't use this because Compiler don;t have any information on what is
  // AnimalType
  // val dog: ac.AnimalType = ???

  // won't work because Compiler is not able to figure out that Cat is subclass of Animal
  // val cat: ac.BoundedAnimal = new Cat

  val pup: ac.SuperBoundedAnimal = new Dog
  val cat: ac.AnimalC = new Cat

  type CatAlias = Cat
  val anotherCat: CatAlias = new Cat

  // Type aliases are used when there are multiple packages with same name/ name conflicts

  // abstract type members: used in api that looks similar to generics
  // alternative to GENERICS
  trait MyList {
    type T
    def add(element: T): MyList
  }

  class NonEmptyList(value: Int) extends MyList {
    override type T = Int
    def add(element: Int): MyList = ???
  }

  // .type
  type CatsType = cat.type  // type alias
//  val newCat: CatsType = cat
//  new CatsType

  /*
    Exercise - enforce a type to be applicable to SOME TYPES only
   */

  // LOCKED
  trait MList {
    type A
    def head: A
    def tail: MList
  }

  // now you need to enforce that MList should be constructed only for
  // Int and not for String

  class CustomList(hd: String, tl: CustomList) extends MList {
    type A = String
    def head = hd
    def tail = tl
  }

  trait ModifiedMList {
    type A <: Number
  }

//  class StringList(hd: String, tl: CustomList) extends ModifiedMList with MList {
//    type A = String
//    def head = hd
//    def tail = tl
//  }

  class IntList(hd: Int, tl: IntList) extends ModifiedMList with MList {
    type A = Integer
    def head = hd
    def tail = tl
  }
}
