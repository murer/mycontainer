#!/bin/bash

mkdir release-logs
rm release-logs/prepare.out release-logs/prepare.err release-logs/perform.out release-logs/perform.err

(mvn release:prepare --batch-mode 1> release-logs/prepare.out 2> release-logs/prepare.err && mvn release:perform --batch-mode 1> release-logs/perform.out 2> release-logs/perform.err &)

