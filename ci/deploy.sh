#!/bin/bash -e

DIR=$( cd "$( dirname ${BASH_SOURCE[0]} )" && pwd )

if [ -d "${DIR}/ROOT" ]; then
    echo "==== Deleting existing ROOT directory ===="
    rm -rf "$DIR/ROOT"
fi

mkdir -p "${DIR}/ROOT"
cd "${DIR}/ROOT"
jar xfv "${WORKSPACE}/miso-web/target/ROOT.war"
sed -r \
    -e 's|localhost:3306/lims|miso-db.res.oicr.on.ca:3306/'\"${DB_NAME}\"'|1' \
    -e 's|(username=)[^=]*$|\1'\"$MYSQL_USER\"'|1' \
    -e 's|(password=)([^=][^ />]*)|\1'\"$MYSQL_PASS\"'|1' \
    <"${DIR}/context.xml" >"${DIR}/ROOT/META-INF/context.xml"


if [ -e "${DIR}/ROOT.war" ]; then
    jar ufv "${DIR}/ROOT.war" -C "${DIR}/ROOT" .
else
    jar cfv "${DIR}/ROOT.war" -C "${DIR}/ROOT" .
fi

rm "${WORKSPACE}/miso-web/target/ROOT.war"
cp "${DIR}/ROOT.war" "${WORKSPACE}/miso-web/target/"
