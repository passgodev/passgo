#!/bin/bash

if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
else
  echo "ERROR | Env file was not found "
fi

sleep 1

./mvnw flyway:migrate \
  -Dflyway.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${POSTGRES_DB} \
  -Dflyway.user=${PG_PASSGO_USER} \
  -Dflyway.password=${PG_PASSGO_PASSWORD}