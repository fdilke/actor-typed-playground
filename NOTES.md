# Notes

* The most important page: [Akka actor typed docs](https://doc.akka.io/docs/akka/current/typed/guide/index.html)
* The implementation of the actor is in the same file as test so you can focus on one file

You can think about `Behaviour[T]` as `T => Next Behavior` (simplification)

Behaviors provides many useful factory methods, for instance:
* Behaviors.same
* Behaviors.receiveMessage
* Behaviors.logMessages