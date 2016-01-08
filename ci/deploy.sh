#!/bin/bash -ex

# Create/Update MySQL db with credentials set in properties file
mysql --host=${DB_HOST} --user=${MYSQL_USER} --password=${MYSQL_PASS} << EOI
DROP DATABASE IF EXISTS $DB_NAME;
CREATE DATABASE $DB_NAME;
GRANT ALL PRIVILEGES ON ${DB_NAME}.* TO ${MYSQL_USER}@${DB_HOST};
FLUSH PRIVILEGES;
EOI
echo "User $MYSQL_USER added to $DB_NAME database"
# Add schemas to lims db
cd ${WORKSPACE}/sqlstore
mvn -P external flyway:migrate -D flyway.user=${MYSQL_USER} -D flyway.pass=${MYSQL_PASS} -D flyway.url=jdbc:mysql://${DB_HOST}/${DB_NAME}
mysql --host=${DB_HOST} --user=${MYSQL_USER} --password=${MYSQL_PASS} -D $DB_NAME < src/test/resources/db/migration/V9000__miso_test_data.test.sql

curl --user ${1} --upload-file "${WORKSPACE}/${2}" --url ${3}
