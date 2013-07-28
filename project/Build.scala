import sbt._
import Keys._


object ApplicationBuild extends Build {

  object V {
    val astyanax = "1.56.42"
  }

  val appName         = "Achaia"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies =  Seq(
    "com.netflix.astyanax" % "astyanax-core" % V.astyanax /*exclude("org.slf4j", "slf4j-log4j12")*/,
    "com.netflix.astyanax" % "astyanax-thrift" % V.astyanax /*exclude("org.slf4j", "slf4j-log4j12")*/,
    "com.netflix.astyanax" % "astyanax-entity-mapper" % V.astyanax /*exclude("org.slf4j", "slf4j-log4j12")*/,
    "org.scala-lang" % "scala-swing" % "2.10.2"
  )

  val testDependencies = Seq("org.specs2" %% "specs2" % "1.11" % "test")


  val buildSettings = Defaults.defaultSettings ++ Seq(
    scalaVersion        := "2.10.2",
    scalacOptions       := Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions", "-language:postfixOps"),
    javacOptions in GlobalScope += "-Djava.library.path=lib",
    javaOptions in Test += "-Dconfig.file=conf/test-application.conf",
    unmanagedClasspath in Runtime += file("conf/")
  )

  val main = Project(id = appName, base = file("."),
    settings = buildSettings ++ Seq(libraryDependencies ++= appDependencies)
  )
}