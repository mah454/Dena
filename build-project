#!/bin/bash

mvn clean compile package install -DskipTest -Dmaven.test.skip=true

rm -rf ./build
mkdir -p ./build/dena/modules/module.a/
mkdir -p ./build/dena/modules/ir.sample/
mkdir -p ./build/dena/framework/
mkdir -p ./build/dena/lib/
mkdir -p ./build/dena/lib/
mkdir -p ./build/dena/conf/

cp -aRvf ./test-module/module-a/target/lib/* ./build/dena/modules/module.a/
cp -aRvf ./test-module/module-a/target/module-a-0.1.jar ./build/dena/modules/module.a/
cp -aRvf ./test-module/module-b/target/module-b-0.1.jar ./build/dena/modules/ir.sample/

# Copy dena-api to shared
mv -vf ./dena-runtime/target/lib/* ./build/dena/lib/

# Copy configs
cp -aRvf ./dena-runtime/src/main/resources/logback.xml ./build/dena/conf/

cp ./dena-runner ./build/dena/
