organization := "org.marktomko"

name := "csvdb"

version := "0.9"

scalaVersion := "2.12.4"

scalacOptions ++= Seq("-Xlint", "-unchecked", "-deprecation", "-feature", "-Xfatal-warnings")

libraryDependencies ++= CsvdbBuild.dependencies

// Tell sbt that we want to see stack traces automatically
traceLevel in run := 0

parallelExecution in Test := true

assemblyJarName in assembly := "../bin/csvdb.jar"
