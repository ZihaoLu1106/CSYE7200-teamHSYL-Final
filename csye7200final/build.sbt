name := """CSYE7200Final"""
organization := "TeamHSYL"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.13"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "3.2.4"// % "provided"
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.2.4" exclude ("org.scala-lang.modules", "scala-xml_2.13")
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.2.4" //% "runtime"
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "4.4.0"
libraryDependencies += "org.mongodb.spark" %% "mongo-spark-connector" % "10.2.2"

dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "2.2.0"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "TeamHSYL.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "TeamHSYL.binders._"
