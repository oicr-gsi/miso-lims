#!/bin/bash -e

# This script is intended to be placed within the tomcat-multi-install
# directory. Its purpose is, given a branch/instance of Tomcat, stop it.

branch=${1}

if [ -d "${branch}" ]; then
    cd ${branch}/tomcat8/bin/
    echo "==== Stopping Tomcat ===="
    ./shutdown.sh
else
    echo "Error: branch/instance not found!"
fi

