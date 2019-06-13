#!/bin/bash

cd $(cd -P -- "$( dirname -- "${BASH_SOURCE[0]}")" && pwd -P)

sbt fastOptJS

cp -p target/scala-2.12/jtop-fastopt.js jtop.js

cat target/scala-2.12/jtop-fastopt.js | sed 's/$g.require/require/g' > jtop.js

node jtop.js
