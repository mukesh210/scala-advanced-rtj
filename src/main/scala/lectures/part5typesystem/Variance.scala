package lectures.part5typesystem

object Variance extends App {

  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // what is variance?
  // "inheritance" - type substitution of generics

  class Cage[T]
  /*
    Since, Cat extends Animal
    Can we replace Cage[Animal] with Cage[Cat] --- this is variance problem
    3 possible solutions:
   */
  // yes - Covariance
  class CCage[+T]
  val ccage: CCage[Animal] = new CCage[Cat]

  // no - Invariance
  class ICage[T]
  // val icage: ICage[Animal] = new ICage[Cat]

  // hell no - opposite = contravariance
  class XCage[-T]
  val xcage: XCage[Cat] = new XCage[Animal]

  class InvariantCage[T](animal: T) // invariant

  // covariant positions
  class CovariantCage[+T](val animal: T)  // COVARIANT POSITION
    // generic type of vals of field is in covariant position

  // class ContravariantCage[-T](val animal: T) // compiler error
  /*
    If above compiler check passes, then below will also work which is wrong
    val catCage: XCage[Cat] = new XCage[Animal](new Crocodile)
   */

  // class CovariantVariableCage[+T](var animal: T) // type of vars are in CONTRAVARIANT POSITION
  /*
    If above works, then below code will also work which is wrong
      val ccage: CCage[Animal] = new CCage[Cat](new Cat)
      ccage.animal = new Crocodile
   */

  //class ContravariantVariableCage[-T](var animal: T)  // also in COVARIANT POSITION
  /*
    If above works for compiler, then below code will also work which is wronG:
    val catCage: XCage[Cat] = new XCage[Animal](new Crocodile)
   */

  class InvariantVariableCage[T](var animal: T) // here, we can assign only single type

//  trait AnotherCovariantCage[+T] {
//    def addAnimal(animal: T)  // contravariant position
//  }
  /*
    val ccage: CCage[Animal] = new CCage[Dog]
    ccage.addAnimal(new Cat)
   */

  class AnotherContravariantCage[-T] {
    def addAnimal(animal: T) = true
  }

  val acc: AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal]
  acc.addAnimal(new Cat)  // cat or below
  class Kitty extends Cat
  acc.addAnimal(new Kitty)

  // HOW to create COVARIANT COLLECTION
  class MyList[+A] {
    def add[B >: A](element: B): MyList[B] = new MyList[B]  // widening the type
    //def add(element: A): MyList[A] = ???
  }

  val emptyList: MyList[Kitty] = new MyList[Kitty]
  val animals: MyList[Kitty] = emptyList.add(new Kitty)
  val moreAnimals: MyList[Cat] = animals.add(new Cat)
  val evenMoreAnimals: MyList[Animal] = moreAnimals.add(new Dog)
  // in above example, compiler is widening the type

  // METHOD ARGUMENTS ARE IN CONTRAVARIANT POSITION

  // method return types
  class PetShop[-T] {
    // def get(isItaPuppy: Boolean): T  // method return types are in covariant position
    /*
      val catShop: PetShop[Cat] = new PetShop[Animal] {
        def get(isItaPuppy: Boolean): Animal = new Cat
       }

       val dogShop: PetShop[Dog] = catShop
       dogShop.get(true)  // EVIL CAT!
     */

    def get[S <: T](isItaPuppy: Boolean, defaultAnimal: S): S = defaultAnimal
  }

  val shop: PetShop[Dog] = new PetShop[Animal]

  // not compile because Cat is not subtype of Dog
  // val evilCat = shop.get(true, new Cat)

  class TerraNova extends Dog
  val bugFurry = shop.get(true, new TerraNova)

  /*
    Big rule:
      - method arguments are in CONTRAVARIANT POSITION
      - return type are in COVARIANT POSITION
   */
  /*
    See code for Function1,.. Function3
    all of them have method parameters in CONTRAVARIANT POSITIONS
    AND RETURN TYPES IN COVARIANT POSITIONS to satisfy compiler constraints
   */

  /**
    * Exercise:
    * 1. Design Invariant, Covariant, Contravariant
    *   Parking[T](things: List[T]) {
    *     park(vehicle: T)
    *     impound(vehicles: List[T])
    *     checkVehicles(conditions: String): List[T]
    *   }
    *
    * 2. used someone else's API: IList[T]
    *
    * 3. Parking = Monad!
    *     - implement flatMap
    */

  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle

  class IList[T]

  class IParking[T](things: List[T]) {
    def park(vehicle: T): IParking[T] = ???
    def impound(vehicles: List[T]): IParking[T] = ???
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => IParking[S]): IParking[S] = ???
  }

  class CParking[+T](things: List[T]) {
    def park[A >: T](vehicle: A): CParking[A] = ???
    def impound[A >: T](vehicles: List[A]): CParking[A] = ???
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => CParking[S]): CParking[S] = ???
  }

  class XParking[-T](things: List[T]) {
    def park(vehicle: T): XParking[T] = ???
    def impound(vehicles: List[T]): XParking[T] = ???
    def checkVehicles[A <: T](conditions: String): List[A] = ???

    // Function1[T, XParking[S]] - here T will become Covariance because of double contravariance
    def flatMap[R <: T, S](f: Function1[R, XParking[S]]): XParking[S] = ???
  }

  /*
  RULE OF THUMB:
    - use covariance = COLLECTION OF THINGS
    - use contravariance = GROUP OF ACTIONS
   */

  // 2.
  class CParking2[+T](things: IList[T]) {
    def park[A >: T](vehicle: A): CParking2[A] = ???
    def impound[A >: T](vehicles: IList[A]): CParking2[A] = ???
    def checkVehicles[A >: T](conditions: String): IList[A] = ???
  }

  class XParking2[-T](things: IList[T]) {
    def park(vehicle: T): XParking2[T] = ???
    def impound[A <: T](vehicles: IList[A]): XParking2[A] = ???
    def checkVehicles[A <: T](conditions: String): IList[A] = ???
  }

  // 3.

}
