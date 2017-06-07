name := "persist"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.2",
  "com.typesafe.akka" %% "akka-persistence" % "2.5.2",
  "com.typesafe.akka" %% "akka-persistence-query" % "2.5.2",
  "com.github.dnvriend" %% "akka-persistence-inmemory" % "2.5.1.1"
)