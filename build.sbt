name := "actor-typed-playground"

version := "0.1"

scalaVersion := "2.12.10"

libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % "2.6.1"
libraryDependencies += "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.6.1" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test
