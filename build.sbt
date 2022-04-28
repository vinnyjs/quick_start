import smithy4s.codegen.Smithy4sCodegenPlugin

ThisBuild / scalaVersion := "2.13.8"

val example = project
  .in(file("modules/example"))
  .enablePlugins(Smithy4sCodegenPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.disneystreaming.smithy4s" %% "smithy4s-http4s" % smithy4sVersion.value,
      "com.disneystreaming.smithy4s" %% "smithy4s-http4s-swagger" % smithy4sVersion.value,
      "org.http4s" %% "http4s-ember-server" % "0.23.11"
    )
  )