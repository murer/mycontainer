#!/bin/bash -e

if grep version pom.xml | grep SNAPSHOT; then
	echo "We will not deploy snapshots to release repository"
	exit 1;
fi

mvn clean verify source:jar-no-fork javadoc:jar gpg:sign deploy:deploy "-Dgpg.passphrase=$MYCONTAINER_SECRET" -DdeployCentral -DaltDeploymentRepository=sonatype-nexus-staging::default::https://oss.sonatype.org/service/local/staging/deploy/maven2 -Dmaven.test.skip.exec -T 10

