ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "BankingSystem"
  )
libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.28"

libraryDependencies += "org.scalafx" %% "scalafx" % "16.0.0-R25"
libraryDependencies ++= Seq(
  "org.openjfx" % "javafx-controls" % "17",
  "org.openjfx" % "javafx-fxml" % "17",
  "org.openjfx" % "javafx-graphics" % "17",
  "org.openjfx" % "javafx-base" % "17",
  "org.openjfx" % "javafx-media" % "17",
  "org.openjfx" % "javafx-web" % "17")