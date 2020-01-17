package org.danielwojda.playground._9

import java.time.ZonedDateTime

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import scala.concurrent.duration._

class SelfHealingClientSpec {
//poc
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

object ReconnectActor {
  case class Reconnect()

  def apply(c: Client): Behavior[Reconnect] = Behaviors.receiveMessage{ _ =>
    c.reconnect()
    Behaviors.same
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
