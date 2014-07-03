#!/bin/bash -xe

if ! mvn clean -B -Dmaven.test.skip.exec -T 10; then
	if ! mvn clean -B -Dmaven.test.skip.exec -T 10; then
		mvn clean -B -Dmaven.test.skip.exec -T 10;
	fi;
fi

if ! mvn install -B -Dmaven.test.skip.exec -T 10; then
	if ! mvn install -B -Dmaven.test.skip.exec -T 10; then
		mvn install -B -Dmaven.test.skip.exec -T 10;
	fi;
fi

