#!/bin/bash -e

TAG="$1"

if [ "x$1" == "x" ]; then
	echo "use: $0 tag-to-be-removed"
	exit 1;
fi;

git tag -d "$TAG"
git push origin ":refs/tags/$TAG"

