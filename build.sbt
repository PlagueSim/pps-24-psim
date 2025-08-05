ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.6"

lazy val root = (project in file("."))
  .settings(
    name := "pps-24-psim",
    mainClass := Some("App"),
    assembly / assemblyJarName := "p-sim.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", _*) => MergeStrategy.discard
      case _                              => MergeStrategy.first
    },
    assembly / test := {}
  )

libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6.1"

libraryDependencies += "org.scalafx" %% "scalafx" % "22.0.0-R33"

libraryDependencies ++= {
  val javafxVersion = "22"
  val classifiers   = Seq("linux", "mac", "win")
  val modules       = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")

  // for every module and every classifier, emit one dependency
  for {
    m  <- modules
    os <- classifiers
  } yield "org.openjfx" % s"javafx-$m" % javafxVersion classifier os
}


libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test
libraryDependencies += "org.typelevel" %% "cats-core" % "2.13.0"
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.6.1"
libraryDependencies += "it.unibo.alice.tuprolog" % "tuprolog" % "3.3.0"
libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
