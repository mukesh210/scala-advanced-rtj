import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {
  def contains(elem: A): Boolean
  def +(elem: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def filter(f: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit
  override def apply(v1: A): Boolean = contains(v1)

  // EXERCISE 2
  def -(elem: A): MySet[A]                  // difference
  def &(anotherSet: MySet[A]): MySet[A]
  def --(anotherSet: MySet[A]): MySet[A]    // intersection

  // EXERCISE 3: Implement Negation operator for the set
  def unary_! : MySet[A]
}

class EmptySet[A] extends MySet[A] {
  override def contains(elem: A): Boolean = false

  override def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  override def map[B](f: A => B): MySet[B] = new EmptySet[B]

  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]

  override def filter(f: A => Boolean): MySet[A] = this

  override def foreach(f: A => Unit): Unit = ()

  override def -(elem: A): MySet[A] = this

  override def &(anotherSet: MySet[A]): MySet[A] = this

  override def --(anotherSet: MySet[A]): MySet[A] = this

  override def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)
}

// all elements of type A which satisfies property:
// {x in A | property(x)}
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
  def contains(elem: A): Boolean = property(elem)

  // {x in A | property(x) || x == elem}
  def +(elem: A): MySet[A] = new PropertyBasedSet[A](x => property(x) || x == elem)

  def ++(anotherSet: MySet[A]): MySet[A] = new PropertyBasedSet[A](x => property(x) || anotherSet(x))

  def map[B](f: A => B): MySet[B] = politelyFail
  def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail
  def foreach(f: A => Unit): Unit = politelyFail

  def filter(f: A => Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && f(x))
  def -(elem: A): MySet[A] = this.filter(x => x != elem)
  def &(anotherSet: MySet[A]): MySet[A] = this.filter(anotherSet)
  def --(anotherSet: MySet[A]): MySet[A] = this.filter(!anotherSet)
  def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

  def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole")
}

class NonEmptySet[A](head: A, remainingSet: MySet[A]) extends MySet[A] {
  override def contains(elem: A): Boolean = elem == head || remainingSet.contains(elem)

  override def +(elem: A): MySet[A] =
    if(this.contains(elem)) this
    else new NonEmptySet[A](elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = remainingSet ++ anotherSet + head

  override def map[B](f: A => B): MySet[B] = remainingSet.map(f) + f(head)

  override def flatMap[B](f: A => MySet[B]): MySet[B] = remainingSet.flatMap(f) ++ f(head)

  override def filter(f: A => Boolean): MySet[A] = {
    val filteredTail = remainingSet.filter(f)
    if(f(head)) filteredTail + head
    else filteredTail
  }

  override def -(elem: A): MySet[A] =
    if(head == elem) remainingSet
    else remainingSet - elem + head

  override def &(anotherSet: MySet[A]): MySet[A] = this.filter(anotherSet)

  override def --(anotherSet: MySet[A]): MySet[A] = this.filter(!anotherSet)

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this.contains(x))

  override def foreach(f: A => Unit): Unit = {
    f(head)
    remainingSet.foreach(f)
  }
}

object MySet {
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] = {
      if(valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)
    }

    buildSet(values.toSeq, new EmptySet[A])
  }
}

object MySetTester extends App {
  val s = MySet(1, 2, 3, 4)
  //s + 5 ++ MySet(5, -1, -2) + 3 flatMap (x => MySet(x, x*10)) filter(_ % 2 == 0) foreach(println)

  val s1 = MySet(3,4,5,6,7,8)

  println("Removing 7")
  s1.-(7).foreach(println)

  println("Intersection of s & s1")
  s.&(s1).foreach(println)

  println("Difference of s from s1")
  s.--(s1).foreach(println)

  val negative = !s // all natural numbers not equal to 1, 2, 3, 4
  println(negative(2))
  println(negative(5))

  val negativeEvens = negative.filter(_ % 2 == 0)
  println(negativeEvens(5))

  val negativeEvens5 = negativeEvens + 5 // all the numbers + 5
  println(negativeEvens5(5))


  val seqExample = Seq(1, 2, 3, 4, 5, 6)
  println(s"SeqExample: ${seqExample(5)}")
  val mapExample = Map(1 -> "Mukesh", 2 -> "Ajay")
}
