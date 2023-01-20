name := "MyProject"

version := "1.0"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.4.1",
  "org.slf4j" % "slf4j-nop" % "2.0.5",
  "com.typesafe.akka" %% "akka-stream" % "2.5.19",
  "com.typesafe.akka" %% "akka-http" % "10.1.7",
  "org.json4s" %% "json4s-jackson" % "4.0.6","joda-time" % "joda-time" % "2.12.2",
  "org.postgresql" % "postgresql" % "42.2.12",
  "io.spray" %%  "spray-json" % "1.3.6"
  /*"com.typesafe.akka" %% "akka-http-core" % "1.0",
  //"com.typesafe.akka" %% "akka-http" % "1.0",
  */
)

