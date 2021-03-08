import play.core.PlayVersion

name := """play-java-hello-world-tutorial"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.4"

libraryDependencies += guice


val akkaVersion =  PlayVersion.akkaVersion

// Some Akka overrides to align versions of artifacts
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
)


libraryDependencies += "commons-io" % "commons-io" % "2.8.0"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.0.0-RC3"

libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % "2.5.32"

// https://mvnrepository.com/artifact/com.twitter.twittertext/twitter-text
libraryDependencies += "com.twitter.twittertext" % "twitter-text" % "2.0.8"

