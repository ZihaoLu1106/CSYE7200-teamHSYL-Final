name := "DataAnalysis"

version := "1.9.7"

Compile / doc / scalacOptions ++= Seq("-Vimplicits", "-deprecation", "-Ywarn-dead-code", "-Ywarn-value-discard", "-Ywarn-unused")

val scalaTestVersion = "3.2.1"

libraryDependencies += "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.0.1"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.0.1"

libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.4.8"


Test / parallelExecution := false
