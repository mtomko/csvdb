import AssemblyKeys._

organization := "org.marktomko"

name := "csvdb"

version := "0.9"

scalaVersion := "2.11.4"

scalacOptions ++= Seq("-Xlint", "-unchecked", "-deprecation", "-feature", "-Xfatal-warnings")

javacOptions in Compile ++= Seq("-source", "1.7", "-target", "1.7", "-Xlint:unchecked", "-Xlint:deprecation")

resolvers ++= CsvdbBuild.resolvers

libraryDependencies ++= CsvdbBuild.dependencies

// Tell sbt that we want to see stack traces automatically
traceLevel in run := 0

parallelExecution in Test := true

jarName in assembly := "../bin/csvdb.jar"

assemblySettings