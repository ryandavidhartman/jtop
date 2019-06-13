enablePlugins(ScalaJSPlugin)

name := "jtop"
scalaVersion := "2.12.8"

version := "0.2-SNAPSHOT"


resolvers += "bintray/non" at "http://dl.bintray.com/non/maven"

libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.7.1"

scalaJSUseMainModuleInitializer := true
mainClass := Some("jtop.Main")

//val copyAndMunge = taskKey[Unit]("")
//
//copyAndMunge := {
//  import java.io._
//  println("HERE")
//  IO.copyFile(new File("target/scala-2.11/jtop-fastopt.js"), new File("jtop.js"))
//}
//
//copyAndMunge := copyAndMunge.triggeredBy(ScalaJSKeys.fastOptJS)

