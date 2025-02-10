// ThisBuild / version := "0.1.0-SNAPSHOT"
// ThisBuild / scalaVersion := "2.12.18"

// lazy val root = (project in file("."))
//   .settings(
//     name := "spark"
//   )

// // Spark dependencies
// libraryDependencies += "org.apache.spark" %% "spark-core" % "3.5.4"
// libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.5.4" % "provided"
// libraryDependencies += "org.apache.spark" %% "spark-streaming" % "3.5.4" % "provided"

// // Spark SQL Kafka dependency
// libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.4"

// libraryDependencies += "org.apache.hadoop" % "hadoop-aws" % "3.4.1"

// // Akka dependencies
// libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.18" // Version stable
// libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.18" // Version stable
// libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "2.1.1" // Version compatible

// // Kafka dependencies
// libraryDependencies += "org.apache.kafka" % "kafka-clients" % "3.7.0"

// // Gson library
// libraryDependencies += "com.google.code.gson" % "gson" % "2.10.1"

// // Logback version compatible with Java 8
// libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3" % Test


name := "hello"
version := "1.0"
scalaVersion := "2.12.18"

resolvers += "MinIO Repository" at "https://dl.min.io/client/spark-select_2.12/"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.5.4" exclude("org.apache.hadoop", "hadoop-client"),
  "org.apache.spark" %% "spark-sql" % "3.5.4",
  "org.apache.spark" %% "spark-avro" % "3.5.4",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.4",  // ✅ Ajout du connecteur Kafka
  "org.apache.hadoop" % "hadoop-aws" % "3.2.0",
  "org.apache.hadoop" % "hadoop-common" % "3.2.0",
  "com.typesafe.akka" %% "akka-stream-kafka" % "2.1.1",
  "org.apache.kafka" % "kafka-clients" % "3.5.1",  // ✅ Mise à jour de kafka-clients
  "com.amazonaws" % "aws-java-sdk" % "1.11.836",
  
  //  Dépendances supplémentaires
  "com.typesafe.akka" %% "akka-actor" % "2.6.19",
  "com.typesafe.akka" %% "akka-stream" % "2.6.19",
  "com.google.code.gson" % "gson" % "2.8.8"
)

dependencyOverrides += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.3.0"
