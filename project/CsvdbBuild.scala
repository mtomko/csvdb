import sbt._

object CsvdbBuild {

  // dependency versions
  val h2Version                   = "1.4.196"
  val jCommanderVersion           = "1.72"
  val jlineVersion                = "2.14.5"
  val scalaArmVersion             = "2.0"
  val scalatestVersion            = "3.0.5"

  // library dependency definitions
  val h2                   = "com.h2database"          %  "h2"                % h2Version
  val jCommander           = "com.beust"               %  "jcommander"        % jCommanderVersion
  val jLine                = "jline"                   %  "jline"             % jlineVersion
  val scalaArm             = "com.jsuereth"            %% "scala-arm"         % scalaArmVersion

  // test dependency definitions
  val scalatest            = "org.scalatest"           %% "scalatest"         % scalatestVersion      % "test"

  val dependencies =
    Seq(
      h2,
      jCommander,
      jLine,
      scalaArm,
      scalatest)

}
