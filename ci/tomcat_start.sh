#!/bin/bash -e

# This script is intended to be placed within the
#             /u/seqprodbio/tomcat-multi-install
# directory. Its purpose is: given a branch/instance of Tomcat, start it.

# This script also handles the manipulation of files FOLLOWING the build
# process

branch=${1}

# Default install directory/war file name
installdir=ROOT


echo "==== Entering webapps directory ===="
cd "/u/seqprodbio/tomcat-multi-install/${branch}/tomcat8/webapps"

if [ ${branch} == oicr ]; then
    installdir="miso-external"
fi

echo "==== Deleting ${installdir} directory ===="
rm -r ${installdir}
echo "==== Making new ${installdir} directory ===="
mkdir ${installdir}
cd ${installdir}
echo "==== Extracting WAR ===="
jar xf "../${installdir}.war"

cd "/u/seqprodbio/tomcat-multi-install"

if [ ${branch} != oicr ]; then
    perl -i -pe "s/\/storage\/miso\//\/u\/seqprodbio\/tomcat-multi-install\/${branch}\/storage\/miso\//g" \
        "${branch}/tomcat8/webapps/ROOT/WEB-INF/classes/miso.properties"
    perl -i -pe "s/your.notification.server/platinum.res.oicr.on.ca/g" \
        "${branch}/tomcat8/webapps/ROOT/WEB-INF/classes/miso.properties"
fi

fi
if [ -d "${branch}" ]; then
    cd ${branch}/tomcat8/bin/
    echo "==== Starting Tomcat ===="
    ./startup.sh
else
    echo "Error: branch/instance not found!"
fi
