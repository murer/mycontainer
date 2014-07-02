#!/bin/bash -xe

mvn clean install -B

find ~/.m2/repository -name "mycontainer" -exec rm -rvf "{}" \; | cat

