name := "dataStore"

version := "1.0"

scalaVersion := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val sprayV = "1.3.3"
  val sparkV = "1.4.1"
  val cassandraConnectorV = "1.4.0-M3"
  val akkaV = "2.4-M3"
  Seq(
    "com.typesafe.akka" % "akka-actor_2.11" % akkaV,
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-testkit" % sprayV % "test",
    "org.specs2" %% "specs2-core" % "2.3.11" % "test",
    "org.apache.spark" %% "spark-core" % sparkV,
    "org.apache.spark" % "spark-sql_2.11" % sparkV,
    //    "org.apache.spark" % "spark-repl_2.11" % sparkV,
    "org.apache.spark" % "spark-streaming_2.11" % sparkV,
    //    "org.apache.spark" % "spark-mllib_2.11" % sparkV,
    "junit" % "junit" % "4.4" % "test",
    "io.spray" %% "spray-json" % "1.3.2",
    "com.datastax.spark" %% "spark-cassandra-connector" % cassandraConnectorV
  )
}

