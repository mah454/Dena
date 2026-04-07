#!/bin/bash

mvn -o clean compile package install -DskipTest -Dmaven.test.skip=true

rm -rf ./dena
mkdir -p ./dena/modules/module.a/
mkdir -p ./dena/modules/ir.sample/
mkdir -p ./dena/framework/
mkdir -p ./dena/lib/shared/
mkdir -p ./dena/lib/system/
mkdir -p ./dena/conf/

cp -aRvf ./test-module/module-a/target/lib/* ./dena/modules/module.a/
cp -aRvf ./test-module/module-a/target/module-a-0.1.jar ./dena/modules/module.a/
cp -aRvf ./test-module/module-b/target/module-b-0.1.jar ./dena/modules/ir.sample/

# Copy dena-api to shared
mv -vf ./dena-runtime/target/lib/dena-api* ./dena/lib/shared/
mv -vf ./dena-runtime/target/lib/*slf4j* ./dena/lib/shared/
mv -vf ./dena-runtime/target/lib/* ./dena/lib/system/

# Copy configs
cp -aRvf ./dena-runtime/src/main/resources/logback.xml ./dena/conf/

cp ./dena-runner ./dena/
