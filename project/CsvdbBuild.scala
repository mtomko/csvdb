import sbt._

object CsvdbBuild {

  // resolvers that we need to find some of our libraries:
  val resolvers = Seq(
    "Typesafe Repository"
      at "http://repo.typesafe.com/typesafe/releases/")

  // dependency versions
  val h2Version                   = "1.4.182"
  val jCommanderVersion           = "1.32"
  val jlineVersion                = "2.12"
  val scalaArmVersion             = "1.4"
//  val scalazVersion               = "7.1.0"
  val scalatestVersion            = "2.2.0"

  // library dependency definitions
  val h2                   = "com.h2database"          %  "h2"                % h2Version
  val jCommander           = "com.beust"               %  "jcommander"        % jCommanderVersion
  val jLine                = "jline"                   %  "jline"             % jlineVersion
  val scalaArm             = "com.jsuereth"            %% "scala-arm"         % scalaArmVersion
//  val scalaz               = "org.scalaz"              %% "scalaz-core"       % scalazVersion

  // test dependency definitions
  val scalatest            = "org.scalatest"           %% "scalatest"         % scalatestVersion      % "test"

  val dependencies =
    Seq(
      h2,
      jCommander,
      jLine,
      scalaArm,
//      scalaz,
      scalatest)

}
