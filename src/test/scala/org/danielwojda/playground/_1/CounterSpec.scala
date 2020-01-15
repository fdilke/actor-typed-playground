package org.danielwojda.playground._1

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Scheduler}
import akka.util.Timeout
import org.scalatest.FlatSpecLike

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

class CounterSpec extends ScalaTestWithActorTestKit with FlatSpecLike {

  "Counter Actor" must "know how to count" in {
    val system = ActorSystem(guardianBehavior = CounterActor(), name = "Counter") // it should be stopped at the end of test
    val actor: ActorRef[CounterActor.Command] = system
    val timeout: Timeout = Timeout(3.seconds)
    val scheduler: Scheduler = system.scheduler
    implicit val ec: ExecutionContextExecutor = system.executionContext

    actor ! CounterActor.Inc      // Fire and forget
    actor ! CounterActor.Inc
    actor.tell(CounterActor.Inc)  // the same as !
    actor ! CounterActor.Inc

    import akka.actor.typed.scaladsl.AskPattern._
    val counterFuture: Future[CounterActor.Counter] =
      actor.ask[CounterActor.Counter](CounterActor.Get)(timeout, scheduler) // Get the answer from the actor

    whenReady(counterFuture){ counter =>
      counter.value shouldBe 4
    }
  }

  it must "know how to count - tested with akka testkit" in {
    val actor = testKit.spawn(CounterActor(), "counter")
    val probe = testKit.createTestProbe[CounterActor.Counter]()

    actor ! CounterActor.Inc
    actor ! CounterActor.Inc
    actor ! CounterActor.Get(probe.ref)

    probe.expectMessage(CounterActor.Counter(2))
  }
}

object CounterActor {
  //Protocol
  sealed trait Command //root type of all incoming messages
  final case object Inc extends Command
  final case class Counter(value: Long) // used as a response only
  final case class Get(replyTo: ActorRef[Counter]) extends Command

  def apply(): Behavior[CounterActor.Command] = counterBehaviour(0)

  private def counterBehaviour(value: Long): Behaviors.Receive[Command] = Behaviors.receiveMessage {
    case Inc =>
      counterBehaviour(value + 1)
    case Get(replyTo) =>
      replyTo ! Counter(value)
      Behaviors.same
  }
}