name := "Personal_finance"

version := "0.1"

scalaVersion := "2.13.10"


val http4sVersion = "1.0.0-M39"

// Only necessary for SNAPSHOT releases
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-ember-server" % http4sVersion,
  "org.http4s" %% "http4s-ember-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,

  // Optional for auto-derivation of JSON codecs
  "io.circe" %% "circe-generic" % "0.14.5",
  // Optional for string interpolation to JSON model
  "io.circe" %% "circe-literal" % "0.14.5",
  "co.fs2" %% "fs2-core" % "3.6.1",
  "co.fs2" %% "fs2-io" % "3.6.1"
)

libraryDependencies ++= Seq(
  "io.github.kirill5k" %% "mongo4cats-core"  % "0.3.0",
  "io.github.kirill5k" %% "mongo4cats-circe" % "0.3.0"
)

