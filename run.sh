#!/bin/bash

java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 --module-path dena-runtime/target/lib -cp .:dena-runtime/target/lib/*.jar -m dena.runtime/ir.moke.dena.MainClass
