package org.danielwojda.playground._1

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Scheduler}
import akka.util.Timeout
import org.scalatest.concurrent.{Futures, ScalaFutures}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._

class CounterSpec extends AnyFlatSpec with Matchers with ScalaFutures {

  "Counter Actor" must "know know how to count" in {
    val system = ActorSystem(CounterActor(), name = "Counter")
    val actor: ActorRef[CounterActor.Command] = system
    implicit val timeout: Timeout = Timeout(3.seconds)
    implicit val scheduler: Scheduler = system.scheduler
    implicit val ec: ExecutionContextExecutor = system.executionContext

    actor ! CounterActor.Inc
    actor ! CounterActor.Inc
    actor.tell(CounterActor.Inc)
    actor ! CounterActor.Inc

    import akka.actor.typed.scaladsl.AskPattern._
    val counterFuture: Future[CounterActor.Counter] =
      actor.ask[CounterActor.Counter](CounterActor.Get)(timeout, scheduler)

    whenReady(counterFuture){ counter =>
      counter.value mustBe 4
    }
  }
}

object CounterActor {
  //Protocol
  sealed trait Command
  final case object Inc extends Command
  final case class Counter(value: Long) // used as a response only
  final case class Get(replyTo: ActorRef[Counter]) extends Command //Returned by the actor, must contain the replay to actor ref

  def apply(): Behavior[CounterActor.Command] = counterBehaviour(0)

  private def counterBehaviour(value: Long): Behaviors.Receive[Command] = Behaviors.receiveMessage {
    case Inc =>
      counterBehaviour(value + 1)
    case Get(replyTo) =>
      replyTo ! Counter(value)
      Behaviors.same
  }
}