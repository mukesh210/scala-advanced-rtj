package lectures.part2afp

object Monads extends App {

  /*
  Monads have 2 properties:
    must have apply methods
    flatMap function should be defined for them
   */
  // our own Try Monad
  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }

  object Attempt {
    def apply[A](a: => A): Attempt[A] = {
      try {
        Success(a)
      } catch {
        case e: Throwable => Fail(e)
      }
    }
  }

  case class Success[+A](value: A) extends Attempt[A] {
    override def flatMap[B](f: A => Attempt[B]): Attempt[B] =
      try {
        f(value)
      } catch {
        case e: Throwable => Fail(e)
      }
  }

  case class Fail(e: Throwable) extends Attempt[Nothing] {
    override def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }

  /*
  Monad laws
  lef-identity

  unit.flatMap(f) = f(x)
  Attempt(x).flatMap(f) = f(x) // success case
  Success(x).flatMap(f) = f(x)  // proved

  right-identity
  attempt.flatMap(unit) = attempt
  Success(x).flatMap(x => Attempt(x)) = Attempt(x) = Success(x)
  Fail(e).flatMap(...) = Fail(e)

  Associativity
  attempt.flatMap(f).flatMap(g) == attemp.flatMap(x => f(x).flatMap(g))

  Fail(e).flatMap(f).flatMap(g) = Fail(e)
  Fail(e).flatMap(x => f(x).flatMap(g)) = Fail(e)

  Success(x).flatMap(f).flatMap(g) = f(x).flatMap(g) or Fail(e)

  Success(x).flatMap(f => f(x).flatMap(g)) =
    f(x).flatMap(g) or Fail(e)
  */

  val attempt = Attempt {
    throw new RuntimeException("My own monad")
  }

  println(attempt)

  /*
  1. Implement a Lazy[T] monad = computation which will only be executed when it;s needed
      unit/apply
      flatMap

  2.  Monad = apply + flatMap
      Monad = apply + map + flatten

      Monad[T] {
        def flatMap[B](f: T => Monad[B]): Monad[B] = ... (implemented)

        def map[B](f: T => B): Monad[B] = {
          flatMap(x => Monad(f(x)))
        }
        def flatten(m : Monad[Monad[B]]): Monad[B] = {
          m.flatMap()
        }

        List(1, 2, 3).flatMap(x => List(x + 1))
          = List(1, 2, 3).map(x => List(x + 1)).flatten
          = List(List(2), List(3), List(4)).flatten
          = List(2, 3, 4)

        f: x => x + 1
        List(1, 2, 3).map(f)
          = List(1, 2, 3).flatMap(x => List(f(x)))
          = List(1, 2, 3).flatMap(x => List(x + 1))

        flatten(List(List(2), List(3), List(4)))
          = x.flatMap(elem => elem)


        (have list in mind)
      }
   */

  // Exercise 1 solution
  class Lazy[+A](value: => A) {
    // call by need
    private lazy val internalValue = value
    def flatMap[B](f: (=> A) => Lazy[B]): Lazy[B] = f(internalValue)
    def use: A = internalValue
  }
  object Lazy {
    def apply[A](value: => A): Lazy[A] = new Lazy[A](value)
  }

  val lazyInstance = Lazy {
    println("I am feeling amazing")
    42
  }

  // println(lazyInstance.use)

  val flatMappedInstance = lazyInstance.flatMap(x => Lazy{
    10*x
  })
  val flatMappedInstance2 = lazyInstance.flatMap(x => Lazy{
    10*x
  })
  flatMappedInstance.use
  flatMappedInstance2.use

  // Exercise 2 solution
  // map and flatten in turns of flatMap
  /*
  Monad[T] {
    def flatMap[B](f: T => Monad[B]): Monad[B] = ... (implemented)

    def map[B](f: T => B): Monad[B] = {
      flatMap(x => Monad(f(x)))
    }
    def flatten(m : Monad[Monad[B]]): Monad[B] = {
      m.flatMap()
    }

    flatten(List(List(2), List(3), List(4)))
      = x.flatMap(elem => elem)


      (have list in mind)
  }
   */
}
