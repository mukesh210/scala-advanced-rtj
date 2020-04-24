package lectures.part4implicits

object OrganizingImplicit extends App {

  // sorted takes Ordering which is implicit
  // this works because by default scala.Predef is imported

  implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  println(List(1, 4, 5, 3, 2).sorted)

  /*
    Implicits value(used as implicit parameters):
      - val/var
      - objects
      - accessors method = defs with no parentheses
   */

  /*
  Exercise
   */

  case class Person(name: String, age: Int)

  val persons = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 66)
  )

//  object Person {
//    implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((x, y) => x.name.compareTo(y.name) < 0)
//  }

  object AlphabeticalNameOdering {
    implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((x, y) => x.name.compareTo(y.name) < 0)
  }

  object AgeOrdering {
    implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((x, y) => x.age < y.age)
  }
  import AgeOrdering._

  println(persons.sorted)

  /*
    Implicit scope --- compiler searches for implicits
    -- normal scope = LOCAL SCOPE (e.g. personOrdering)
    -- imported scope (future executionContext)
    -- companion object of all types involved in the method signature
       -- List
       -- Ordering
       -- all the types involved = A or any superType
   */
  // sorted[B >: A](implicit ord: Ordering[B]): List

  /*
    EXERCISE
      Add 3 ordering by 3 different criteria
      -- totalPrice = most used(50%)
      -- by unit count = 25%
      -- by unit price = 25%
   */

  case class Purchase(nUnits: Int, unitPrice: Double)

  object Purchase {
    implicit val purchaseOdering: Ordering[Purchase] = Ordering.fromLessThan((x, y) => x.nUnits*x.unitPrice < y.nUnits*y.unitPrice)
  }

  object UnitOrdering {
    implicit val purchaseOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.nUnits < _.nUnits)
  }

  object PriceOrdering {
    implicit val purchaseOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.unitPrice < _.unitPrice)
  }

  // this proves that local scope > companion object
  import UnitOrdering._

  val purchasesList = List(
    Purchase(4, 4000),
    Purchase(5, 200),
    Purchase(2, 2000)
  )

  println(s"PurchaseList Sorted: ${purchasesList.sorted}")
}
