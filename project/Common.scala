import sbt.{Defaults, _}
import sbt.Keys._
import sbt.plugins.JvmPlugin

object Common extends AutoPlugin {

	override def trigger: PluginTrigger = allRequirements

  	override def requires: Plugins = JvmPlugin

  	override val projectSettings: scala.Seq[sbt.Def.Setting[_]] =
      Dependencies.Common ++
  		Seq (
  		 organization := "com.alphasystem.game.uno",
        organizationName := "Alphasystem",
        scalaVersion := "2.13.1",
        scalacOptions ++= Seq(
          "-encoding",
          "UTF-8",
          "-feature",
          "-unchecked",
          "-deprecation",
          //"-Xfatal-warnings",
          "-Xlint",
          "-Yno-adapted-args",
          "-Ywarn-dead-code",
          "-Xfuture",
          "-target:jvm-1.8"
        ),
        javacOptions in compile ++= Seq(
          "-Xlint:unchecked"
        )
    )
}