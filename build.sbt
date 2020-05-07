import Dependencies._

lazy val common = createProject("common", "common")

lazy val server = createProject("server", "server", Server)
  .aggregate(common)
  .dependsOn(common)
  .enablePlugins(JavaAppPackaging)

lazy val client = createProject("client", "client", JavaFx, Client)
  .aggregate(common)
  .dependsOn(common)
  .enablePlugins(JavaAppPackaging)

lazy val modules: Seq[ProjectReference] = Seq(common, server, client)

lazy val uno = project
  .in(file("."))
  .aggregate(modules: _*)

def createProject(projectId: String, _moduleName: String, additionalSettings: sbt.Def.SettingsDefinition*): Project = {
  Project(id = projectId, base = file(projectId))
    .settings(
      name := projectId,
      moduleName := _moduleName
    )
    .settings(additionalSettings: _*)
}