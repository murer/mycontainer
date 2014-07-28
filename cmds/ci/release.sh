#!/bin/bash -xe 

./cmds/ci/script.sh
cp cmds/ci/settings.priv $HOME/.m2/settings.xml
./cmds/central/deploy-release.sh 
rm $HOME/.m2/settings.xml

