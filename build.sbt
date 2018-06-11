lazy val akkaHttpVersion = "10.1.0"
lazy val akkaVersion    = "2.5.11"

lazy val jsonParsingLibrary =
  "com.typesafe.play" %% "play-json" % "2.6.9"

lazy val root =
  (project in file("."))
    .settings(
      inThisBuild(List(
        organization    := "org.ecomm",
        scalaVersion    := "2.12.4"
      )),
      name := "akka-http-seed",
      resolvers +=
        Resolver.bintrayRepo("hseeberger", "maven"),
      libraryDependencies ++=
        Seq(
          "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
          "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
          "com.typesafe.akka" %% "akka-slf4j"           % akkaVersion,
          "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
          "de.heikoseeberger" %% "akka-http-play-json"  % "1.21.0",
          "ch.qos.logback"    % "logback-classic"       % "1.2.3",

          "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
          "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
          "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
          "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test) :+ jsonParsingLibrary,
      scalacOptions ++=
        Seq(
          "-unchecked",
          "-deprecation",
          "-Xlint",
          "-Ywarn-dead-code",
          "-language:_",
          "-target:jvm-1.8",
          "-encoding", "UTF-8")
    )
