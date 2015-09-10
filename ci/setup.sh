#!/bin/bash -e

# This script will, given parameters representing the different branches 
# create the necessary Tomcat8 installation directories as well as configure
# the ports accordingly. This script will also configure Tomcat according to
# MISO configurations.

cd /storage/
install="tomcat-multi-install" # default Tomcat install location

if [ -d "$install" ]; then
    echo -e "==== Tomcat install directory already exists ===="
    echo -e "==== Skipping download ====\n"
else
    echo -e "==== Creating Tomcat installation directory ===="
    mkdir $install
fi

cd $install

if [ -d "instances" ]; then
    echo -e "==== Instances file already exists ===="
    echo -e "==== Skipping ====\n"
else
    echo -e "==== Creating Instances file ===="
    touch instances
fi

tomcat="apache-tomcat-8.0.26" # update to latest Tomcat version if available

# Fetch tomcat8 (may have to update wget url)
if [ -e "tomcat8" ]; then
    echo -e "==== Using predownloaded Tomcat8 ====\n"
else
    echo -e "==== Downloading Tomcat8 ===="
    wget http://apache.mirror.iweb.ca/tomcat/tomcat-8/v8.0.26/bin/${tomcat}.tar.gz
    if [ $? != 0 ]; then
        echo -e "Error: download link may have to be updated in script!"
        exit 1
    fi
    echo -e "==== Unpacking Tomcat8 ===="
    tar -zxf ${tomcat}.tar.gz
    mv ${tomcat} tomcat8
    echo -e "==== Deleting Tomcat Junk ====\n"
    rm -r tomcat8/webapps/host-manager
    rm -r tomcat8/webapps/docs
    rm -r tomcat8/webapps/examples
    rm -r tomcat8/webapps/ROOT
    cd tomcat8/lib/
    wget https://repos.tgac.ac.uk/miso/common/mysql-connector-java-5.1.10.jar
    wget https://repos.tgac.ac.uk/miso/common/jndi-file-factory-1.0.jar
    cd ../..
    mkdir -p tomcat8/conf/Catalina/localhost/
fi


defStartPort=8080    # default Tomcat START/connector port
defStopPort=8005     # default Tomcat SHUTDOWN port
defAJPPort=8009      # default Tomcat AJP port
defRedirectPort=8443 # default Tomcat Redirect port

# Variables for the _new_ ports for each Tomcat installation
startPort=8080
stopPort=7080
AJPPort=8009
redirectPort=8443

IFS=$'\n'
for branch in $(cat instances); do
    echo -e "==== $branch branch already exists! ===="
    startPort=$((startPort+1))
    stopPort=$((stopPort+1))
    AJPPort=$((AJPPort+1))
    redirectPort=$((redirectPort+1))
done

echo -e "\n"

for branch do 
    if [ -d "$branch" ]; then
        echo -e "==== Skipping $branch branch ====\n"
    else
        echo -e "\n==== Making new ${branch} branch ===="
        mkdir $branch
        cp -r tomcat8 $branch/
        echo -e "==== Entering ${branch} branch ===="
        cd $branch/tomcat8/conf
        echo -e "==== Finding and Replacing Port numbers ===="
        sed -i "s/${defStartPort}/${startPort}/" server.xml
        sed -i "s/${defStopPort}/${stopPort}/" server.xml
        sed -i "s/${defAJPPort}/${AJPPort}/" server.xml
        sed -i "s/${defRedirectPort}/${redirectPort}/" server.xml
        echo -e "==== Port numbers replaced ===="
        startPort=$((startPort+1))
        stopPort=$((stopPort+1))
        AJPPort=$((AJPPort+1))
        redirectPort=$((redirectPort+1))
        cd ../../..
        echo $branch >> instances
    fi
done

echo -e "\n==== Setup Complete! ===="
