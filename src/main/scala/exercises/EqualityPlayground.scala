package exercises

import lectures.part4implicits.TypeClasses.{ User}

object EqualityPlayground extends App {
  trait Equal[T] {
    def apply(firstValue: T, secondValue: T): Boolean
  }

  object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]) = equalizer.apply(a, b)
  }

  // TYPE CLASS INSTANCES
  object UserNameEquality extends Equal[User] {
    override def apply(firstValue: User, secondValue: User): Boolean = firstValue.name == secondValue.name
  }

  implicit object UserNameEmailEquality extends Equal[User] {
    override def apply(firstValue: User, secondValue: User): Boolean =
      firstValue.name == secondValue.name &&
        firstValue.email == secondValue.email
  }

  val firstUser = User("Mukesh", 25, "mukesh@gmail.com")
  val secondUser = User("Mukesh", 25, "mukeshCopy@gmail.com")

  // AD-HOC Polymorphism
  println(Equal[User](firstUser, firstUser))

  /*
    Exercise: improve the Equal TC with an implicit conversion class
      ===(anotherValue: T)
      !==(anotherValue: T)
   */

  implicit class TypeSafeEqual[T](value: T) {
    def ===(anotherValue: T)(implicit equalizer: Equal[T]) = equalizer.apply(value, anotherValue)

    def !==(anotherValue: T)(implicit equalizer: Equal[T]) = !equalizer.apply(value, anotherValue)
  }

  println("firstUser equals to secondUser ::", firstUser === secondUser)
  /*
    firstUser.===(secondUser)
    new TypeSafeEqual[User](john).===(secondUser)
    new TypeSafeEqual[User](john).===(secondUser)(UserNameEmailEquality)
   */
  println("firstUser not equals to secondUser ::", firstUser !== secondUser)

  /*
    TYPE SAFE
   */
  println(firstUser == 43)  // compiles
  // println(firstUser === 43) // won;t compile so type safe
}
