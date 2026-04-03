#!/bin/bash

java --module-path dena-runtime/target/lib -cp .:dena-runtime/target/lib/*.jar -m dena.runtime/ir.moke.dena.MainClass
