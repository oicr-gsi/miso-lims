#!/bin/bash -e

DIR=$( cd "$( dirname ${BASH_SOURCE[0]} )" && pwd )
curl --user "jenkins:deployer" --upload-file "${DIR}/ROOT.war" --url ${1}

