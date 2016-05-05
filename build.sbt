name := "dataStore"

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val sprayV = "1.3.3"
  val sparkV = "1.6.1"
  val cassandraConnectorV = "1.6.0-M2"
  val akkaV = "2.4-M3"
  val scoptV = "3.3.0"
  Seq(
    "org.slf4j" % "slf4j-api" % "1.7.13",
    "ch.qos.logback" % "logback-core" % "1.1.1",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    //    "com.typesafe.akka" % "akka-actor_2.11" % akkaV,
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-testkit" % sprayV % "test",
    "org.specs2" %% "specs2-core" % "2.3.11" % "test",
    "org.apache.spark" %% "spark-core" % sparkV exclude("org.slf4j", "slf4j-log4j12"),
    "org.apache.spark" %% "spark-sql" % sparkV,
    //    "org.apache.spark" % "spark-repl_2.11" % sparkV,
    "org.apache.spark" %% "spark-streaming" % sparkV,
    //    "org.apache.spark" % "spark-mllib_2.11" % sparkV,
    "junit" % "junit" % "4.4" % "test",
    "io.spray" %% "spray-json" % "1.3.2",
    "org.apache.spark" %% "spark-streaming" % sparkV,
    "com.datastax.spark" %% "spark-cassandra-connector" % cassandraConnectorV,
    "com.github.scopt" %% "scopt" % scoptV


  )
}



assemblyJarName in assembly := "dataPlatform.jar"

test in assembly := {}

unmanagedResourceDirectories in Compile += { baseDirectory.value / "src/config/" }

assemblyMergeStrategy in assembly := {
  case PathList("org", "apache", xs@_*) => MergeStrategy.first
  case PathList("io", "dropwizard", xs@_*) => MergeStrategy.first
  case PathList("io", "netty", xs@_*) => MergeStrategy.first
  case PathList("com", "google", "common", "base", xs@_*) => MergeStrategy.first
  case PathList("com", "codahale", "metrics", xs@_*) => MergeStrategy.first
  case PathList("com", "esotericsoftware", "minlog", xs@_*) => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".html" => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".properties" => MergeStrategy.first
  case "application.conf" => MergeStrategy.concat
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}