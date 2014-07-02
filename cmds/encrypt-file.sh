#!/bin/bash

if [ "x$MYCONTAINER_SECRET" == "x" ]; then
        echo "export MYCONTAINER_SECRET to descrypt files";
        exit 1;
fi

openssl enc -aes-256-cbc -salt -in "$1" -out "$1.crypt" -pass "pass:$REPOZSECRET";

