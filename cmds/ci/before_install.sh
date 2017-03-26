#!/bin/bash

java --version
mvn --version

echo "$MYCONTAINER_SECRET"
echo "$REPOZSECRET"

./cmds/decrypt.sh

gpg --import keys/pyrata.org.priv
