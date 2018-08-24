#!/bin/bash -xe

mvn clean -B -Dmaven.test.skip.exec

mvn install -B -Dmaven.test.skip.exec
