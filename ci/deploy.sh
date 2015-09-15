#!/bin/bash -e

DIR=$( cd "$( dirname ${BASH_SOURCE[0]} )" && pwd )
curl --user ${1} --upload-file "${DIR}/${2}" --url ${3}

