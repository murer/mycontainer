#!/bin/bash -xe

source mvn_ops.sh

./cmds/decrypt.sh

gpg --import keys/pyrata.org.priv

cp cmds/ci/settings.priv $HOME/.m2/settings.xml
./cmds/central/deploy-release.sh
rm $HOME/.m2/settings.xml
