package org.danielwojda.playground._0

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import org.scalatest.FlatSpecLike

class PrintActorSpec extends ScalaTestWithActorTestKit with FlatSpecLike {

  "Print Actor" must "accepts messages" in {
    val actor = testKit.spawn(PrintActor(), "print-actor")

    actor ! PrintActor.PrintMe("Hello")
    actor ! PrintActor.PrintMe("World")
    actor ! PrintActor.PrintMe("!!!")
  }

  object PrintActor {
    case class PrintMe(msg: String)

    def apply(): Behavior[PrintMe] = Behaviors.receiveMessage { printMe =>
      println(printMe.msg)
      Behaviors.same
    }
  }
}
