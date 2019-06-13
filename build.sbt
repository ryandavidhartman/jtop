enablePlugins(ScalaJSPlugin)

name := "jtop"
scalaVersion := "2.12.8"

version := "1.0"

resolvers += "bintray/non" at "http://dl.bintray.com/non/maven"

scalaJSUseMainModuleInitializer := true
mainClass in Compile := Some("jtop.JTopApp")

//val copyAndMunge = taskKey[Unit]("")
//
//copyAndMunge := {
//  import java.io._
//  println("HERE")
//  IO.copyFile(new File("target/scala-2.11/jtop-fastopt.js"), new File("jtop.js"))
//}
//
//copyAndMunge := copyAndMunge.triggeredBy(ScalaJSKeys.fastOptJS)

