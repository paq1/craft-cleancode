ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

val projectName = "craft-cleancode"

lazy val app = (project in file("src/app"))
  .dependsOn(core)
  .settings(
    name := s"$projectName-app"
  )

lazy val core = (project in file("src/core"))
  .settings(
    name := s"$projectName-core",
    libraryDependencies ++= List(
      "ch.qos.logback" % "logback-classic" % "1.4.14",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "org.scalamock" %% "scalamock" % "5.2.0" % Test
    )
  )
