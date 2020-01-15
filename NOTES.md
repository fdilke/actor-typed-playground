# Notes

## Actor

## Actor System

## Error handling

By default, if there is an exception thrown inside a typed actor, the actor is stopped.

## Other
 
* The most important page: [Akka actor typed docs](https://doc.akka.io/docs/akka/current/typed/guide/index.html)
* The implementation of the actor is in the same file as test so you can focus on one file

You can think about `Behaviour[T]` as `T => Next Behavior` (simplification)

Behaviors provides many useful factory methods, for instance:
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