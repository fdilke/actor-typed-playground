# Notes

## Actor

Stateful beings
Has Mailbox (message queue), FIFO order
Processes one message at a time !!!
Lightweight
Isolated
State can be change only be sending(receiving) a message
Communicate with other actors only through messages
Don't wait for the response from other actor

Allowed operations:
* create another actor
* send a message
* change it's behavior

Issues:
- be aware about default conf of error handling
- actor's mailbox overflowing
- Akka api and implementation can be intimidating for a new Scala devs, however it's a great source of good practices

Actors are autonomous and concurrent objects which execute asynchronously. The actors model provides flexible mechanisms for building parallel and distributed software systems.
Source: [Actor Model white paper](http://wiki.c2.com/?ActorsModel)

## Why Actors

* to store "shared" mutable state
* to model a State Machine (FSM).
* to simplify the world and forget about concurrency

## New in Typed Actors

* message type check (!)
* sender must be a part of your protocol
* behaviors *always* returns new behavior (can be the same, but must be returned)

## Error handling

By default, if there is an exception thrown inside a typed actor, the actor is stopped.

## Other

* The most important page: [Akka actor typed docs](https://doc.akka.io/docs/akka/current/typed/guide/index.html)
* The implementation of the actor is in the same file as test so you can focus on one file

You can think about `Behaviour[T]` as `T => Next Behavior` (simplification)

[Behaviors](https://doc.akka.io/api/akka/current/akka/actor/typed/scaladsl/Behaviors$.html) provides many useful factory methods, for instance:
* Behaviors.same
* Behaviors.receiveMessage
* Behaviors.logMessages
* Behaviors.supervise - define what to do in case of error


akka.actor.typed.ActorRef
An ActorRef is the identity or address of an Actor instance. It is valid only during the Actor’s lifetime and allows messages to be sent to that Actor instance. Sending a message to an Actor that has terminated before receiving the message will lead to that message being discarded; such messages are delivered to the [[DeadLetter]] channel. [Source](https://doc.akka.io/api/akka/current/akka/actor/typed/ActorRef.html)

ActorContext
* creating a child actor
* logging a msg
*

# Docs

* [Scheduling messages to self](https://doc.akka.io/docs/akka/current/typed/interaction-patterns.html#scheduling-messages-to-self)
* [Send Future result to self](https://doc.akka.io/docs/akka/current/typed/interaction-patterns.html#send-future-result-to-self)
