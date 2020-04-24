package lectures.part5typesystem

object FBoundedPolymorphism extends App {

//  trait Animal {
//    def breed: List[Animal]
//  }
//
//  class Cat extends Animal {
//    override def breed: List[Animal] = ???  // List[Cat]
//  }
//
//  class Dog extends Animal {
//    override def breed: List[Animal] = ???  // List[Dog]
//  }

  /*
    How do we force Compiler to force us to return List[Cat] from Cat class
    and List[Dog] from Dog class.
    Right now, we can return List[Dog] from Cat class which we don't want
   */

  // Naive solution
//  trait Animal {
//    def breed: List[Animal]
//  }
//
//  class Cat extends Animal {
//    override def breed: List[Dog] = ???  // List[Cat]
//  }
//
//  class Dog extends Animal {
//    override def breed: List[Cat] = ???  // List[Dog]
//  }

  // Solution 2 - F-Bounded Polymorphism
//  trait Animal[A <: Animal[A]] {  // recursive types = F-Bounded Polymorphism
//    def breed: List[Animal[A]]
//  }
//
//  class Cat extends Animal[Cat] {
//    override def breed: List[Cat] = ???  // List[Cat]
//  }
//
//  class Dog extends Animal[Dog] {
//    override def breed: List[Dog] = ???  // List[Dog]
//  }
//
//  trait Entity[E <: Entity[E]]  // ORM
//  class Person extends Comparable[Person] { // FBP
//    override def compareTo(o: Person): Int = ???
//  }
//
//  // in solution(2), how do we ensure that Crocodile extends Animal[Crocodile]?
//  class Crocodile extends Animal[Dog] {
//    override def breed: List[Dog] = ???  // List[Dog]
//  }

  // Solution #3 - FBP + self-types
  // whatever descendent of Animal A i am writing, they must also be an A
//  trait Animal[A <: Animal[A]] { self: A =>
//    def breed: List[Animal[A]]
//  }
//
//  class Cat extends Animal[Cat] {
//    override def breed: List[Cat] = ???  // List[Cat]
//  }
//
//  class Dog extends Animal[Dog] {
//    override def breed: List[Dog] = ???  // List[Dog]
//  }

//  class Crocodile extends Animal[Dog] {
//    override def breed: List[Dog] = ???  // List[Dog]
//  }

//  trait Fish extends Animal[Fish]
//  class Shark extends Fish {
//    override def breed: List[Animal[Fish]] = List(new Cod)
//  }
//  class Cod extends Fish {
//    override def breed: List[Animal[Fish]] = ???
//  }

  // Exercise

  // solution 4: type classes!
//  trait Animal
//  trait CanBreed[A] {
//    def breed(a: A): List[A]
//  }
//
//  class Dog extends Animal
//  object Dog {
//    implicit object DogCanBreed extends CanBreed[Dog] {
//      override def breed(a: Dog): List[Dog] = List()
//    }
//  }
//
//  implicit class CanBreedOps[A](animal: A) {
//    def breed(implicit canBreed: CanBreed[A]): List[A] =
//      canBreed.breed(animal)
//  }
//
//  val dog = new Dog
//  dog.breed
//
//  class Cat extends Animal
//  object Cat {
//    implicit object CatsCanBreed extends CanBreed[Dog] {
//      override def breed(a: Dog): List[Dog] = List()
//    }
//  }
//
//  val cat = new Cat
  // cat.breed  // compiler error

  // solution 5
  trait Animal[A] { // pure type classes
    def breed(a: A): List[A]
  }

  class Dog
  object Dog {
    implicit object DogAnimal extends Animal[Dog] {
      override def breed(a: Dog): List[Dog] = List()
    }
  }

  class Cat
  object Cat {
    implicit object DogAnimal extends Animal[Dog] {
      override def breed(a: Dog): List[Dog] = List()
    }
  }

  implicit class AnimalOps[A](animal: A) {
    def breed(implicit animalTypeClassInstance: Animal[A]): List[A] =
      animalTypeClassInstance.breed(animal)
  }

  val dog = new Dog
  dog.breed

//  val cat = new Cat
//  cat.breed
}
