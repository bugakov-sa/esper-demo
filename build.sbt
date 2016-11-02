name := "esper-demo"
version := "1.0"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  //complex event processing
  "com.espertech" % "esper" % "5.5.0",
  "cglib" % "cglib-nodep" % "2.2",
  "org.antlr" % "antlr4-runtime" % "4.1",
  //multithreading
  "com.typesafe.akka" %% "akka-actor" % "2.4.10",
  //logging
  "ch.qos.logback" %  "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
)

packAutoSettings