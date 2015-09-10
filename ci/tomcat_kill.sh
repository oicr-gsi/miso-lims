#!/bin/bash -e

# This script is intended to be placed within the tomcat-multi-install
# directory. Its purpose is to kill ALL instances of Tomcat

# Usage: If there are port errors occuring during the build process

pkill -f catalina
pkill -f tomcat
