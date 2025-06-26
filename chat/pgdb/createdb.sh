#!/bin/sh

psql << EOF

CREATE DATABASE $DB_NAME OWNER postgres;

EOF

pg_restore -d $DB_NAME /docker-entrypoint-initdb.d/$DB_NAME.dump
