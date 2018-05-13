name := "heraion"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.6"

organizationName := "Hackenfreude"

startYear := Some(2018)

licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))

libraryDependencies ++= Seq(
  "io.circe" %% "circe-parser" % "0.9.3",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)

lazy val heraion = project
  .in(file("."))
  .enablePlugins(AutomateHeaderPlugin)