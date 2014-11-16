import AssemblyKeys._

organization := "org.marktomko"

name := "csvdb"

version := "0.9"

scalaVersion := "2.11.4"

scalacOptions ++= Seq("-Xlint", "-unchecked", "-deprecation", "-feature", "-Xfatal-warnings")

resolvers ++= CsvdbBuild.resolvers

libraryDependencies ++= CsvdbBuild.dependencies

// Tell sbt that we want to see stack traces automatically
traceLevel in run := 0

parallelExecution in Test := true

jarName in assembly := "../bin/csvdb.jar"

assemblySettings

//wartremoverErrors ++= Warts.all

//wartremoverExcluded ++= Seq("csvdb.Args")