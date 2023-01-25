name := "MyProject"

version := "1.0"

scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.4.1",
  "org.slf4j" % "slf4j-nop" % "2.0.5",
  "com.typesafe.akka" %% "akka-stream" % "2.7.0",
  "com.typesafe.akka" %% "akka-http" % "10.4.0",
  "org.json4s" %% "json4s-jackson" % "4.0.6","joda-time" % "joda-time" % "2.12.2",
  "org.postgresql" % "postgresql" % "42.5.1",
  "io.spray" %%  "spray-json" % "1.3.6",
  "org.scalatest" %% "scalatest" % "3.1.1" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % "10.4.0" % Test
  /*"com.typesafe.akka" %% "akka-http-core" % "1.0",
  //"com.typesafe.akka" %% "akka-http" % "1.0",
  */
)

