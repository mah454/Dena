#!/bin/bash

mvn -o clean compile package install -DskipTest -Dmaven.test.skip=true

rm -rf ./app
mkdir -p ./app/modules/module.a/
mkdir -p ./app/modules/ir.sample/
mkdir -p ./app/framework/
mkdir -p ./app/lib/shared/
mkdir -p ./app/lib/system/
mkdir -p ./app/conf/

cp -aRvf ./test-module/module-a/target/lib/* ./app/modules/module.a/
cp -aRvf ./test-module/module-a/target/module-a-0.1.jar ./app/modules/module.a/
cp -aRvf ./test-module/module-b/target/module-b-0.1.jar ./app/modules/ir.sample/

# Copy dena-api to shared
mv -vf ./dena-runtime/target/lib/dena-api* ./app/lib/shared/
mv -vf ./dena-runtime/target/lib/*slf4j* ./app/lib/shared/
mv -vf ./dena-runtime/target/lib/* ./app/lib/system/

# Copy configs
cp -aRvf ./dena-runtime/src/main/resources/logback.xml ./app/conf/

cp ./dena-runner ./app/
