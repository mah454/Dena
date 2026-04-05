#!/bin/bash

mvn -o clean compile package install -DskipTest -Dmaven.test.skip=true

rm -rf ./modules/*
mkdir ./modules/module.a
mkdir ./modules/ir.sample

cp -aRvf ./test-module/module-a/target/lib/* ./modules/module.a/
cp -aRvf ./test-module/module-a/target/module-a-0.1.jar ./modules/module.a/
cp -aRvf ./test-module/module-b/target/module-b-0.1.jar ./modules/ir.sample/

# Copy dena-api to shared
mv -vf ./dena-api/target/dena-api-0.1.jar ./shared/
mv -vf ./dena-runtime/target/lib/*slf4j* ./shared/

clear
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 \
  -p shared \
  --add-modules ALL-MODULE-PATH \
  -cp .:dena-runtime/target/lib/* \
  ir.moke.dena.MainClass
