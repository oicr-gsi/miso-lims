# MISO Continuous Integration
How to setup and configure MISO continuous integration.

See [OICR wiki](https://wiki.oicr.on.ca/display/GSI/MISO+LIMS+Continuous+Integration) for complete step-by-step instructions.

## Prerequisites
For our continuous integration we assume the following are installed and configured properly:
1. Jenkins (miso-ci.res.oicr.on.ca)

This is our continuous integration tool of choice. It manages building and deploying MISO from a Git repository to a Tomcat instance (and much more).
    * SonarQube

2. Git Repository branch to be built

4. Debian 8 Machine

5. Nginx

We use Nginx to proxy-forward our MISO instance to an alias.

6. Tomcat 8 Server (as a system service)

We use Tomcat to deploy and host our MISO instance.

## Rundown/Process of Continuous Integration Jenkins Job

* Pull code from desired branch on commit (or some other trigger)
* Build using ```mvn clean install -P external```
* Edit ```context.xml``` and inject into WAR for deployment (```editContext.sh```)
* Deploy the WAR to Tomcat 8 instance

## Quick Setup Guide from Scratch
Assuming that Debian, Tomcat 8, Nginx, Jenkins and Jenkins plug-ins are installed and configured properly, we create a new Maven project in Jenkins.

Give the Maven project a name and description.

Select the GitHub project to build. In our case https://github.com/oicr-gsi/miso-lims, next we select _Git_ for our SCM. For the repository URL enter \<your-repository\>.git. For us: https://github.com/oicr-gsi/miso-lims.git . Then select which branch you want to build.

### Build Triggers
Next, select whichever triggers you would like. We chose _Build when a change is pushed to GitHub_.

### Build Environment
We check off _Inject environment variables to the build process_. We then define the following variables in _Properties Content_

```bash
DB_HOST=         # database host
DB_NAME=         # database name
MYSQL_USER=      # MySQL user for the provided database
WAR_PATH=        # Path to the WAR that you would like to deploy (should be ci/ROOT.war)
TOMCAT_USERPASS= # Username and Password for Tomcat Manager (should be username:password)
SERVER=          # http://your.server.here:8080/manager/text/deploy?path=/ROOT&update=true
```
**Note:** we need to define the Tomcat user credentials to deploy with. Add the following to ```$CATALINA_HOME/conf/tomcat_users.xml```:

```xml
<role rolename="manager-script"/>
<user username="admin" password="admin" roles="manager-script"/>
```

the corresponding ```TOMCAT_USERPASS``` for the above would be _admin:admin_.

Then check the _Inject passwords to the build as environment variables_ option and add a Job password with the name ```MYSQL_PASS```. Provide the corresponding password.

### Build
You can leave the _Root POM_ field blank or just enter ```pom.xml```. For goals and options enter: ```clean install -P external```.

### Post Steps
Add an _Execute Shell_ post-build step and enter the following command:

```bash -x ci/editContext.sh```

Add another _Execute Shell_ post-build step and enter the following command:

```bash -x ci/deploy.sh ${TOMCAT_USERPASS} ${WAR_PATH} ${SERVER}```

### Post-build Actions
**Optional:** Add SonarQube.

## Setting up a new MISO instance from Existing Resources

* Acquire a copy of an existing MISO server instance from IT
* Copy an existing Jenkins job (e.g. TGAC Develop)
* Modify the ```SERVER``` and other necessary environment variables
