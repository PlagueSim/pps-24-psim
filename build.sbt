ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.6"

lazy val root = (project in file("."))
  .settings(
    name := "pps-24-psim"
  )

libraryDependencies += "org.scalafx" %% "scalafx" % "22.0.0-R33"

libraryDependencies ++= {
  // Determine OS version of JavaFX binaries
  lazy val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux") => "linux"
    case n if n.startsWith("Mac") => "mac"
    case n if n.startsWith("Windows") => "win"
    case _ => throw new Exception("Unknown platform!")
  }
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
    .map(m => "org.openjfx" % s"javafx-$m" % "16" classifier osName)
}

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test
libraryDependencies += "org.typelevel" %% "cats-core" % "2.13.0"
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.6.1"
libraryDependencies ++= Seq(
  "dev.optics" %% "monocle-core"  % "3.3.0",
  "dev.optics" %% "monocle-macro" % "3.3.0",
)