import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._
import net.virtualvoid.sbt.graph.Plugin._


object ApplicationBuild extends Build {

  object V {
    val astyanax = "1.56.42"
  }

  val appName         = "Achaia"
  val appVersion      = "1.0.2-SNAPSHOT"

  val appDependencies =  Seq(
    "com.netflix.astyanax" % "astyanax-core" % V.astyanax /*exclude("org.slf4j", "slf4j-log4j12")*/,
    "com.netflix.astyanax" % "astyanax-thrift" % V.astyanax exclude("javax.servlet", "servlet-api"),
    "com.netflix.astyanax" % "astyanax-entity-mapper" % V.astyanax /*exclude("org.slf4j", "slf4j-log4j12")*/,
    "org.scala-lang" % "scala-swing" % "2.10.2",
//    "com.github.myst3r10n" % "moreswing-swing" % "0.1.2",
    "com.github.benhutchison" % "scalaswingcontrib" % "1.4",
    "com.github.myst3r10n" % "moreswing-swing_2.10" % "0.1.2",
    "org.swinglabs" % "swingx" % "1.6.1",
    "com.typesafe.akka" % "akka-actor_2.10" % "2.2.0",
    "com.google.code.gson" % "gson" % "2.2.4",
    "org.simplericity.macify" % "macify" % "1.0"
  )

//  resolvers += "Sonatype OSS" at "https://oss.sonatype.org/content/groups/public"

  val testDependencies = Seq("org.specs2" %% "specs2" % "1.11" % "test")


  val buildSettings = Defaults.defaultSettings ++ Seq(
    version := appVersion,
    exportJars := true,
    scalaVersion        := "2.10.2",
//    scalacOptions       := Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions", "-language:postfixOps"),
//    javacOptions in GlobalScope += "-Djava.library.path=lib",
    javacOptions ++= Seq("-source", "1.6") ++ Seq("-target", "1.6"),
    javaOptions in Test += "-Xdock:name=\"Alessio\" -Dconfig.file=conf/test-application.conf",
    mainClass in assembly := Some("Main"),
    mergeStrategy in assembly <<= (mergeStrategy in assembly)(old => {
        case PathList("javax.servlet", "servlet-api", xs @ _*) => MergeStrategy.first
        case x => old(x)
    }),
    unmanagedClasspath in Runtime += file("conf/")
  )

  val main = Project(id = appName, base = file("."),
    settings = buildSettings ++ assemblySettings ++ Seq(libraryDependencies ++= appDependencies) ++ graphSettings
  ).settings(resolvers += "simplericity" at "http://simplericity.org/repository/")
}