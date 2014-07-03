#!/bin/bash -e

mvn clean verify source:jar-no-fork javadoc:jar gpg:sign deploy:deploy "-Dgpg.passphrase=$MYCONTAINER_SECRET" -DdeployCentral -DaltDeploymentRepository=sonatype-nexus-staging::default::https://oss.sonatype.org/service/local/staging/deploy/maven2

