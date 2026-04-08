#!/bin/bash

./make-project.sh

rm -rf docker/dena
cp -aRvf build/dena docker/
(
  cd docker
  ./generate-jre.sh
  docker build . -t dena:latest
)
