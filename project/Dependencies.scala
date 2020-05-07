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
  private val ScalacheckVersion = "1.14.3"
  private val LogbackVersion = "1.2.3"
  private val JavaFxVersion = "14.0.1"
  private val ScalaFxVersion = "12.0.2-R18"
  private val ControlsFxVersion = "11.0.1"
  private val ComTypesafeAkka = "com.typesafe.akka"
  private val IoCirce = "io.circe"
  private val ComBeachape = "com.beachape"
  private val DeHeikoseeberger = "de.heikoseeberger"
  private val OrgScalatest = "org.scalatest"
  private val OrgScalacheck = "org.scalacheck"
  private val ChQosLogback = "ch.qos.logback"
  private val OrgOpenJfx = "org.openjfx"
  private val OrgScalaFx = "org.scalafx"
  private val OrgControlsFx = "org.controlsfx"

  val Common: Seq[Setting[Seq[ModuleID]]] = Seq(
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
      ChQosLogback        %  "logback-classic"     % LogbackVersion,
      ComTypesafeAkka     %% "akka-stream-testkit" % AkkaVersion              % Test,
      ComTypesafeAkka     %% "akka-http-testkit"   % AkkaHttpVersion          % Test,
      OrgScalatest        %% "scalatest"           % ScalatestVersion         % Test,
      OrgScalacheck       %% "scalacheck"          % ScalacheckVersion        % Test
    )
  )

  val Server: Seq[Setting[Seq[ModuleID]]] = Seq(
    libraryDependencies ++= Seq(
      DeHeikoseeberger    %% "akka-http-circe"             % AkkaHttpCirceVersion,
      ComTypesafeAkka     %% "akka-cluster-sharding-typed" % AkkaVersion,
      ComTypesafeAkka     %% "akka-actor-testkit-typed"    % AkkaVersion      % Test
    )
  )


  val JavaFx: Seq[Setting[Seq[ModuleID]]] = {
    // Add OS specific JavaFX dependencies
    val osName = System.getProperty("os.name") match {
      case n if n.startsWith("Linux") => "linux"
      case n if n.startsWith("Mac") => "mac"
      case n if n.startsWith("Windows") => "win"
      case _ => throw new Exception("Unknown platform!")
    }
    val javafxModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web").map(module => s"javafx-$module")
    Seq(
      libraryDependencies ++= javafxModules.map(moduleName => OrgOpenJfx % moduleName % JavaFxVersion classifier osName)
    )
  }

  val Client: Seq[Setting[Seq[ModuleID]]] = Seq(
    libraryDependencies ++= Seq(
      OrgScalaFx     %% "scalafx"     % ScalaFxVersion,
      OrgControlsFx  % "controlsfx"   % ControlsFxVersion
    )
  )
}
