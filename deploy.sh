#!/bin/sh

set -e

export PROJECT_VERSION=${TRAVIS_TAG}
echo "Project version is \"$PROJECT_VERSION\""

lein deploy
