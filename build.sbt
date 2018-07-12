name := """employeeHandling"""
organization := "Softcaribbean"

version := "1.0-SNAPSHOT"

// Common project
lazy val common = project.in(file("./common")).
  enablePlugins(PlayScala)

scalaVersion := "2.12.6"

libraryDependencies in common += ws
libraryDependencies in common += guice
libraryDependencies in common += openId
libraryDependencies in common += "com.typesafe.akka" %% "akka-actor" % "2.5.13"
libraryDependencies in common += "com.typesafe.play" %% "play-json" % "2.6.0"
libraryDependencies in common += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test


// EmployeeApi project
lazy val employeeApi = project.in(file("./employeeApi")).
  dependsOn(common).
  enablePlugins(PlayScala)

scalaVersion := "2.12.6"

libraryDependencies in employeeApi += "com.typesafe.play" %% "play-jdbc" % "2.6.15"
libraryDependencies in employeeApi += "org.postgresql" % "postgresql" % "42.2.2"

// AuthApi project
lazy val authApi = project.in(file("./authApi")).
  dependsOn(common).
  enablePlugins(PlayScala)

scalaVersion in authApi := "2.12.6"

libraryDependencies in authApi += "com.typesafe.play" %% "play-jdbc" % "2.6.15"
libraryDependencies in authApi += "org.postgresql" % "postgresql" % "42.2.2"


// Site project
lazy val site = project.in(file("./site")).
  dependsOn(common).
  enablePlugins(PlayScala)

scalaVersion in site := "2.12.6"

libraryDependencies in site += "org.webjars" % "bootstrap" % "3.3.4"


lazy val root = project.in(file(".")).aggregate(
  authApi,
  employeeApi,
  site,
  common
)
