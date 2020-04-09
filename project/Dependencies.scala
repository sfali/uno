import sbt._
import Keys._

object Dependencies {

  private val AkkaVersion = "2.6.4"
  private val AkkaHttpVersion = "10.1.11"
  private val CirceVersion = "0.13.0"
  private val EnumeratumVersion = "1.5.15"
  private val EnumeratumCirceVersion = "1.5.23"
  private val AkkaHttpCirceVersion = "1.31.0"
  private val ScalatestVersion = "3.3.0-SNAP2"
  private val ComTypesafeAkka = "com.typesafe.akka"
  private val IoCirce = "io.circe"
  private val ComBeachape = "com.beachape"
  private val DeHeikoseeberger = "de.heikoseeberger"
  private val OrgScalatest = "org.scalatest"

  val Common = Seq(
    // These libraries are added to all modules via the `Common` AutoPlugin
    libraryDependencies ++= Seq(
      ComTypesafeAkka     %% "akka-actor-typed"    % AkkaVersion,
      ComTypesafeAkka     %% "akka-stream-typed"   % AkkaVersion,
      ComTypesafeAkka     %% "akka-http"           % AkkaHttpVersion,
      IoCirce             %% "circe-core"          % CirceVersion,
      IoCirce             %% "circe-generic"       % CirceVersion,
      IoCirce             %% "circe-parser"        % CirceVersion,
      ComBeachape         %% "enumeratum"          % EnumeratumVersion,
      ComBeachape         %% "enumeratum-circe"    % EnumeratumCirceVersion,
      DeHeikoseeberger    %% "akka-http-circe"     % AkkaHttpCirceVersion,
      ComTypesafeAkka     %% "akka-stream-testkit" % AkkaVersion              % Test,
      ComTypesafeAkka     %% "akka-http-testkit"   % AkkaHttpVersion          % Test,
      OrgScalatest        %% "scalatest"           % ScalatestVersion         % Test
    )
  )

  val Client = Seq (
    libraryDependencies ++= Seq(
      "org.scalafx"    %% "scalafx"    % "12.0.2-R18",
      "org.controlsfx" %  "controlsfx" % "11.0.1"
    )
  )
}
