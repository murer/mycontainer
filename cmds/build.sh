#!/bin/bash -xe

source mvn_ops.sh
mvn clean install -Dmaven.test.skip.exec -T 10 && mvn test

