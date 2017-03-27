#!/bin/bash -xe

source mvn_ops.sh

mvn test -B

find "$HOME/.m2/repository" -name "mycontainer" -exec rm -rf "{}" \; || true
