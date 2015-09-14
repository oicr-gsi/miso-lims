#!/bin/bash -e

DIR=$( cd "$( dirname ${BASH_SOURCE[0]} )" && pwd )

if [ -d "${DIR}/ROOT" ]; then
    echo "==== Deleting existing ROOT directory ===="
    rm -rf "$DIR/ROOT"
fi

mkdir -p "${DIR}/ROOT"
cd "${DIR}/ROOT"
jar xf "${WORKSPACE}/miso-web/target/ROOT.war"


perl -i -pe "s/devlims/$DB_NAME/g" "${WORKSPACE}/ci/context.xml"
perl -i -pe "s/username-here/$MYSQL_USER/g" "${WORKSPACE}/ci/context.xml"
perl -i -pe "s/password-here/$MYSQL_PASS/g" "${WORKSPACE}/ci/context.xml"

    <"${DIR}/context.xml" >"${DIR}/ROOT/META-INF/context.xml"
cp "${DIR}/context.xml" "${DIR}/ROOT/META-INF/context.xml"

if [ -e "${DIR}/ROOT.war" ]; then
    jar uf "${DIR}/ROOT.war" -C "${DIR}/ROOT" .
else
    jar cf "${DIR}/ROOT.war" -C "${DIR}/ROOT" .
fi

rm "${WORKSPACE}/miso-web/target/ROOT.war"
cp "${DIR}/ROOT.war" "${WORKSPACE}/miso-web/target/"
