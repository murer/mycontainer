#!/bin/bash -xe

./cmds/decrypt.sh

gpg --import keys/pyrata.org.priv

cp cmds/ci/settings.priv $HOME/.m2/settings.xml
./cmds/central/deploy-snapshot.sh
rm $HOME/.m2/settings.xml
