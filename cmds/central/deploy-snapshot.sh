#!/bin/bash -e

mvn -B clean verify source:jar-no-fork javadoc:jar gpg:sign deploy:deploy "-Dgpg.passphrase=$MYCONTAINER_SECRET" -DdeployCentral -DaltDeploymentRepository=sonatype-nexus-snapshots::default::https://oss.sonatype.org/content/repositories/snapshots -Dmaven.test.skip.exec -T 10
