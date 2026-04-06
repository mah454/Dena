#!/bin/bash

mvn -o clean compile package install -DskipTest -Dmaven.test.skip=true

rm -rf ./dena
mkdir -p ./dena/lib/modules/module.a/
mkdir -p ./dena/lib/modules/ir.sample/
mkdir -p ./dena/lib/shared/
mkdir -p ./dena/lib/system/
mkdir -p ./dena/lib/framework/

cp -aRvf ./test-module/module-a/target/lib/* ./dena/lib/modules/module.a/
cp -aRvf ./test-module/module-a/target/module-a-0.1.jar ./dena/lib/modules/module.a/
cp -aRvf ./test-module/module-b/target/module-b-0.1.jar ./dena/lib/modules/ir.sample/

# Copy dena-api to shared
mv -vf ./dena-runtime/target/lib/dena-api* ./dena/lib/shared/
mv -vf ./dena-runtime/target/lib/*slf4j* ./dena/lib/shared/
mv -vf ./dena-runtime/target/lib/* ./dena/lib/system/

cp ./dena-runner ./dena/
