#!/bin/bash -xe

mvn clean install -B

find "$HOME/.m2/repository" -name "mycontainer" -exec rm -rvf "{}" \; | cat

