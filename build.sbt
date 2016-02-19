import sbt._
import sbt.Keys._
import sbt._
import sbtassembly.Plugin.AssemblyKeys._
import sbtassembly.Plugin._

val myScalaVersion = "2.11.7"
val myAkkaVersion = "2.3.14"
val myPlayVersion = "2.4.6"

lazy val scalajsOutputDir = Def.settingKey[File]("Directory for Javascript files output by ScalaJS")

lazy val trifecta_js = (project in file("app-js"))
  .settings(
    name := "trifecta_js",
    organization := "com.github.ldaniels528",
    version := "0.19.0",
    scalaVersion := myScalaVersion,
    relativeSourceMaps := true,
    persistLauncher := true,
    persistLauncher in Test := false,
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
    libraryDependencies ++= Seq(
      "com.github.ldaniels528" %%% "scalascript" % "0.2.20",
      "com.vmunier" %% "play-scalajs-sourcemaps" % "0.1.0" exclude("com.typesafe.play", "play_2.11"),
      "org.scala-js" %%% "scalajs-dom" % "0.9.0",
      "be.doeraene" %%% "scalajs-jquery" % "0.9.0"
    ))
  .enablePlugins(ScalaJSPlugin)

lazy val coreDeps = Seq(
  //
  // ldaniels528 Dependencies
  "com.github.ldaniels528" %% "commons-helpers" % "0.1.2",
  "com.github.ldaniels528" %% "tabular" % "0.1.3" exclude("org.slf4j", "slf4j-log4j12"),
  //
  // Akka dependencies
  "com.typesafe.akka" %% "akka-actor" % myAkkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % myAkkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % myAkkaVersion % "test",
  //
  // Avro Dependencies
  "com.twitter" %% "bijection-core" % "0.9.0",
  "com.twitter" %% "bijection-avro" % "0.9.0",
  "org.apache.avro" % "avro" % "1.8.0",
  //
  // JSON dependencies
  "com.typesafe.play" %% "play-json" % myPlayVersion,
  //
  // Kafka and Zookeeper Dependencies
  "com.101tec" % "zkclient" % "0.7" exclude("org.slf4j", "slf4j-log4j12"),
  "org.apache.curator" % "curator-framework" % "2.7.1",
  "org.apache.curator" % "curator-test" % "2.7.1",
  "org.apache.kafka" %% "kafka" % "0.9.0.0" exclude("org.slf4j", "slf4j-log4j12"),
  "org.apache.kafka" % "kafka-clients" % "0.9.0.0",
  "org.apache.zookeeper" % "zookeeper" % "3.4.7" exclude("org.slf4j", "slf4j-log4j12"),
  //
  // SQL/NOSQL Dependencies
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.9",
  "org.mongodb" %% "casbah-commons" % "2.8.0" exclude("org.slf4j", "slf4j-log4j12"),
  "org.mongodb" %% "casbah-core" % "2.8.0" exclude("org.slf4j", "slf4j-log4j12"),
  //
  // General Scala Dependencies
  "org.scalaz" %% "scalaz-core" % "7.2.0",
  //
  // General Java Dependencies
  "joda-time" % "joda-time" % "2.9.1",
  "org.joda" % "joda-convert" % "1.8.1",
  "org.slf4j" % "slf4j-api" % "1.7.12",
  "net.liftweb" %% "lift-json" % "3.0-M7",
  //
  // Testing dependencies
  "junit" % "junit" % "4.12" % "test",
  "org.mockito" % "mockito-all" % "1.10.19" % "test",
  "org.scalatest" %% "scalatest" % "2.2.3" % "test"
)

lazy val trifecta_cli = (project in file("app-cli"))
  .settings(
    name := "trifecta_cli",
    organization := "com.github.ldaniels528",
    version := "0.19.0",
    scalaVersion := myScalaVersion,
    scalacOptions ++= Seq("-deprecation", "-encoding", "UTF-8", "-feature", "-target:jvm-1.7", "-unchecked",
      "-Ywarn-adapted-args", "-Ywarn-value-discard", "-Xlint"),
    javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked", "-source", "1.7", "-target", "1.7", "-g:vars"),
    assemblySettings,
    mainClass in assembly := Some("com.github.ldaniels528.trifecta.TrifectaShell"),
    test in assembly := {},
    jarName in assembly := "trifecta_" + version.value + ".bin.jar",
    mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) => {
      case PathList("stax", "stax-api", xs@_*) => MergeStrategy.first
      case PathList("log4j-over-slf4j", xs@_*) => MergeStrategy.discard
      case PathList("META-INF", "MANIFEST.MF", xs@_*) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
    },
    resolvers += "google-sedis-fix" at "http://pk11-scratch.googlecode.com/svn/trunk",
    resolvers += "clojars" at "https://clojars.org/repo",
    resolvers += "conjars" at "http://conjars.org/repo",
    libraryDependencies ++= coreDeps ++ Seq(
      //
      // General Scala Dependencies
      "org.mashupbots.socko" %% "socko-webserver" % "0.6.0",
      "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
      //
      // Storm Dependencies
      "org.apache.storm" % "storm-core" % "0.9.3"
        exclude("org.apache.zookeeper", "zookeeper")
        exclude("org.slf4j", "log4j-over-slf4j")
        exclude("commons-logging", "commons-logging"),
      //
      // General Java Dependencies
      "org.scala-lang" % "jline" % "2.10.6",
      "org.fusesource.jansi" % "jansi" % "1.11"
    )
  )

lazy val trifecta_ui = (project in file("app-play"))
  .dependsOn(trifecta_cli)
  .settings(
    name := "trifecta_ui",
    organization := "com.github.ldaniels528",
    version := "0.19.0",
    scalaVersion := myScalaVersion,
    scalacOptions ++= Seq("-deprecation", "-encoding", "UTF-8", "-feature", "-target:jvm-1.7", "-unchecked",
      "-Ywarn-adapted-args", "-Ywarn-value-discard", "-Xlint"),
    javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked", "-source", "1.7", "-target", "1.7", "-g:vars"),
    relativeSourceMaps := true,
    scalajsOutputDir := (crossTarget in Compile).value / "classes" / "public" / "javascripts",
    pipelineStages := Seq(gzip, /*htmlMinifier,*/ uglify),
    Seq(packageScalaJSLauncher, fastOptJS, fullOptJS) map { packageJSKey =>
      crossTarget in(trifecta_js, Compile, packageJSKey) := scalajsOutputDir.value
    },
    compile in Compile <<=
      (compile in Compile) dependsOn (fastOptJS in(trifecta_js, Compile)),
    ivyScala := ivyScala.value map (_.copy(overrideScalaVersion = true)),
    resolvers += "google-sedis-fix" at "http://pk11-scratch.googlecode.com/svn/trunk",
    libraryDependencies ++= coreDeps ++ Seq(cache, filters, json, ws,
      //
      // Web Jar dependencies
      //
      "org.webjars" % "angularjs" % "1.4.8",
      "org.webjars" % "angularjs-nvd3-directives" % "0.0.7-1",
      "org.webjars" % "angularjs-toaster" % "0.4.8",
      "org.webjars" % "angular-highlightjs" % "0.4.3",
      "org.webjars" % "angular-ui-bootstrap" % "0.14.3",
      "org.webjars" % "angular-ui-router" % "0.2.13",
      "org.webjars" % "bootstrap" % "3.3.6",
      //"org.webjars" % "d3js" % "3.5.3",
      "org.webjars" % "font-awesome" % "4.5.0",
      "org.webjars" % "highlightjs" % "8.7",
      "org.webjars" % "jquery" % "2.1.3",
      "org.webjars" % "nervgh-angular-file-upload" % "2.1.1",
      "org.webjars" %% "webjars-play" % "2.4.0-1"
    ))
  .enablePlugins(PlayScala, play.twirl.sbt.SbtTwirl, SbtWeb)
  .aggregate(trifecta_js)

// loads the jvm project at sbt startup
onLoad in Global := (Command.process("project trifecta_ui", _: State)) compose (onLoad in Global).value
