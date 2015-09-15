#!/bin/bash -e

curl --user "jenkins:deployer" --upload-file "ROOT.war" --url ${1}

