name := "dataStore"

version := "1.0"

scalaVersion := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-testkit" % sprayV % "test",
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "org.specs2" %% "specs2-core" % "2.3.11" % "test",
    "org.apache.spark" %% "spark-core" % "1.4.1",
    "junit" % "junit" % "4.4" % "test",
    "io.spray" %% "spray-json" % "1.3.2",
    "com.datastax.spark" %% "spark-cassandra-connector" % "1.4.0-M3"
  )
}

