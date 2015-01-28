jtop
=============

An htop-style terminal app for JMX enabled JVM apps.

Setup on OS X:

```sh
brew install node npm
npm install jmx blessed
```

## Running jtop

From terminal A:

```sh
sbt ~fastOptJS
```

From terminal B:

```sh
sbt fastOptStage::run
```

## Launch test java application (Scala REPL)
```sh
export JAVA_OPTS="-Dcom.sun.management.jmxremote.port=8855 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false" scala
scala
```

## List available MBeans from Scala REPL

```sh
scala
:load list-mbeans.scala
```