#!/bin/bash -xe

mvn test -B

find "$HOME/.m2/repository" -name "mycontainer" -exec rm -rf "{}" \; || true
