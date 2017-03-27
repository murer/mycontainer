#!/bin/bash -xe

source mvn_opts.sh

mvn test -B

find "$HOME/.m2/repository" -name "mycontainer" -exec rm -rf "{}" \; || true
