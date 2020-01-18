package org.danielwojda.playground._9

import java.time.ZonedDateTime

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import org.scalatest.FlatSpecLike

import scala.concurrent.duration._

class SelfHealingClientSpec  extends ScalaTestWithActorTestKit with FlatSpecLike {

  "Self healing client" must "reconnect if does not receive pong in 10s" in {
    val mainActor: ActorRef[Unit] = testKit.spawn(MainActor(), "main")

    Thread.sleep(20000)
  }
}

object Client {
  case class Ping(id: Long)
  case class Pong(id: Long)
}
trait Client { //Great types, by the way
  def sendPing(p: Client.Ping): Unit
  def handlePong(handler: Client.Pong => Unit): Unit
  def reconnect(): Unit
}
class TestClient extends Client {
  override def sendPing(p: Client.Ping): Unit = println("Sending Ping...")
  override def handlePong(handler: Client.Pong => Unit): Unit = ???
  override def reconnect(): Unit = println("reconnecting...")
}

object MainActor {
  def apply(): Behavior[Unit] =
    Behaviors.setup { context =>
      val client = new TestClient()
      val reconnectRef = context.spawn(ReconnectActor(client), "reconnect-actor")
      val pongActor = context.spawn(PongActor(reconnectRef), "pong-actor")
      Behaviors.ignore
    }
}

object ReconnectActor {
  sealed trait Command
  case class Reconnect() extends Command
  case class SendPing() extends Command

  def apply(c: Client): Behavior[Command] = Behaviors.withTimers{ scheduler =>
    scheduler.startTimerAtFixedRate(SendPing(), 5.seconds)
    behavior(c)
  }

  private def behavior(c: Client): Behaviors.Receive[Command] = {
    Behaviors.receiveMessage {
      case Reconnect() =>
        c.reconnect()
        behavior(c)
      case SendPing() =>
        c.sendPing(Client.Ping(7))
        behavior(c)
    }
  }
}

object PongActor {
  sealed trait Command
  case object PongReceived extends Command
  case object CheckLastPong extends Command

  def apply(reconnect: ActorRef[ReconnectActor.Reconnect]): Behavior[Command] =
    Behaviors.withTimers { scheduler =>
      scheduler.startTimerAtFixedRate(CheckLastPong, 10.seconds)
      pong(reconnect, lastPong = ZonedDateTime.now())
    }

  def pong(reconnect: ActorRef[ReconnectActor.Reconnect], lastPong: ZonedDateTime): Behavior[Command] =
      Behaviors.receiveMessage {
        case CheckLastPong =>
          if(lastPong.plusSeconds(10).isBefore(ZonedDateTime.now())) {
            reconnect ! ReconnectActor.Reconnect()
          }
          Behaviors.same
        case PongReceived =>
          pong(reconnect, lastPong = ZonedDateTime.now())
      }
}
