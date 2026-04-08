#!/bin/bash

rm -rf jre

MODULES=$(java --list-modules | cut -d@ -f1 | grep -vE "jdk.compiler|jdk.javadoc|jdk.jdeps|jdk.jlink|jdk.jshell|jdk.jartool")

jlink \
  --module-path $JAVA_HOME/jmods \
  --add-modules $(echo $MODULES | tr " " ",") \
  --strip-debug \
  --no-header-files \
  --no-man-pages \
  --compress=2 \
  --output jre
