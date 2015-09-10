#!/bin/bash -e

# This script is intended to be placed within the ci directory of the MISO
# directory. Its purpose is to find and replace the hostname in the
# ExternalSection Controller Helper Service source file with an appropriate
# server

echo "==== Replacing hostname in Controller Helper Service ===="
perl -pi -e "s/hostname-here/miso-demo.res.oicr.on.ca/g" \
    "${WORKSPACE}/miso-external-web/src/main/java/uk/ac/tgac/bbsrc/miso/external/ajax/ExternalSectionControllerHelperService.java"
