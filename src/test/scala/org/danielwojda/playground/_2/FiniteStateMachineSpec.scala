package org.danielwojda.playground._2

import akka.actor.testkit.typed.scaladsl.{ManualTime, ScalaTestWithActorTestKit}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import org.scalatest.FlatSpecLike

import scala.concurrent.duration._

/**
 * https://doc.akka.io/docs/akka/2.6.1/typed/fsm.html
 * https://doc.akka.io/docs/akka/current/typed/testing-async.html
 */
class FiniteStateMachineSpec extends ScalaTestWithActorTestKit(ManualTime.config) with FlatSpecLike {

  val manualTime: ManualTime = ManualTime()

  "Rate Limiter Actor" must "drop a lot of messages" in {
    val probe = testKit.createTestProbe[RateLimiter.Request]()
    val actor = testKit.spawn(RateLimiter(probe.ref), "rate-limiter")

    actor ! RateLimiter.Request("1")
    actor ! RateLimiter.Request("2")
    actor ! RateLimiter.Request("3")
    actor ! RateLimiter.Request("4")
    actor ! RateLimiter.Request("5")
    manualTime.timePasses(5.seconds)
    actor ! RateLimiter.Request("6")

    probe.expectMessage(RateLimiter.Request("1"))
    probe.expectMessage(RateLimiter.Request("2"))
    probe.expectMessage(RateLimiter.Request("3"))
    probe.expectMessage(RateLimiter.Request("6"))
  }
}

object RateLimiter {
  val Threshold = 3

  sealed trait Command
  case class Request(id: String) extends Command
  case object Activate extends Command

  def apply(client: ActorRef[Request]): Behavior[Command] = activated(client, 0)

  def activated(client: ActorRef[Request], noSentRequest: Long): Behavior[Command] = Behaviors.receiveMessage {
    case request: Request =>
      if (noSentRequest < Threshold) {
        client ! request
        activated(client, noSentRequest + 1)
      } else {
        deactivated(client)
      }
    case Activate =>
      Behaviors.same
  }

  def deactivated(client: ActorRef[Request]): Behavior[Command] =
    Behaviors.withTimers[Command] { scheduler =>
      scheduler.startSingleTimer(Activate, 5.seconds)
      Behaviors.receiveMessage {
        case _: Request =>
          // drop the request
          Behaviors.same
        case Activate =>
          activated(client, 0)
      }

    }
}
