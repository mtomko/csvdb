import sbt._

object CsvdbBuild {

  // resolvers that we need to find some of our libraries:
  val resolvers = Seq(
    "Typesafe Repository"
      at "http://repo.typesafe.com/typesafe/releases/")

  // dependency versions
  val grizzledSlf4jVersion        = "1.0.2"
  val h2Version                   = "1.4.182"
  val jCommanderVersion           = "1.32"
  val jlineVersion                = "2.12"
  val logbackVersion              = "1.0.13"
  val scalaArmVersion             = "1.4"
  val slf4jVersion                = "1.7.6"
  val scalatestVersion            = "2.2.0"

  // library dependency definitions
  //val grizzledSlf4j        = "org.clapper"             %% "grizzled-slf4j"    % grizzledSlf4jVersion
  val h2                   = "com.h2database"          %  "h2"                % h2Version
  //val jCommander           = "com.beust"               %  "jcommander"        % jCommanderVersion
  val jLine                = "jline"                   %  "jline"             % jlineVersion
  //val logback              = "ch.qos.logback"          %  "logback-core"      % logbackVersion
  //val logbackClassic       = "ch.qos.logback"          %  "logback-classic"   % logbackVersion
  val scalaArm             = "com.jsuereth"            %% "scala-arm"         % scalaArmVersion
  //val slf4j                = "org.slf4j"               %  "slf4j-api"         % slf4jVersion

  // test dependency definitions
  val scalatest            = "org.scalatest"           %% "scalatest"         % scalatestVersion      % "test"

  val dependencies =
    Seq(
      //grizzledSlf4j,
      h2,
      //jCommander,
      jLine,
      //logback,
      //logbackClassic,
      //slf4j,
      scalaArm,
      scalatest)

}
