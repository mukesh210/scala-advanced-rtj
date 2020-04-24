package lectures.part5typesystem

object SelfTypes extends App {

  // requiring a type to be mixed in

  trait Instrumentalist {
    def play(): Unit
  }

  // Suppose we want that whoever is extending Singer must also extend Instrumentalist
  trait Singer { self: Instrumentalist => // SELF TYPES: whoever implements Singer to implement Instrumentalist
    def sing(): Unit
  }

  class LeadSinger extends Singer with Instrumentalist {
    override def play(): Unit = ???
    override def sing(): Unit = ???
  }

  // Illegal
//  class Vocalist extends Singer {
//    override def sing(): Unit = ???
//  }

  val jamesHetfield = new Singer with Instrumentalist {
    override def play(): Unit = ???
    override def sing(): Unit = ???
  }

  class Guitarist extends Instrumentalist {
    override def play(): Unit = println("Guitar Solo")
  }

  val ericClapton = new Guitarist with Singer {
    override def sing(): Unit = ???
  }

  // self types vs inheritance
  class A
  class B extends A // B is an A

  trait T
  trait S { self: T => }  // S REQUIRES T

  // self types are used in CAKE PATTERN => "dependency injection"

  // DI
  class Component {
    // API
  }
  class ComponentA extends Component
  class ComponentB extends Component
  class DependentComponent(val component: Component)

  // CAKE PATTERN
  trait ScalaComponent {
    // API
    def action(x: Int): String
  }
  trait ScalaDependentComponent { self: ScalaComponent => // self typing allows us to use methods of ScalaComponent
    def dependentAction(x: Int): String = action(x) + " this rocks!"
  }
  trait ScalaApplication { self: ScalaDependentComponent => }

  // layer 1 - small components
  trait Pictures extends ScalaComponent
  trait Stats extends ScalaComponent

  // layer 2 - compose
  trait Profile extends ScalaDependentComponent with Pictures
  trait Analytics extends ScalaDependentComponent with Stats

  // layer 3 - app
  trait AnalyticsApp extends ScalaApplication with Analytics

  // above, we can see that we can mixin any trait we want thereby forming layers above layers - CAKE PATTERN

  /*
    CAKE PATTERN VS DI
      DI - dependency is injected by framework dynamically
      CAKE PATTERN - Type is checked at compile time... if we don't mixin self-types, compiler would give error
   */

  // cyclical dependencies  - will give error when ran
//  class X extends Y
//  class Y extends X
// above cyclical dependencies is possible with self-types

  trait X { self: Y => }
  trait Y { self: X => }
}
