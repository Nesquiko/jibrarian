#! /usr/bin/env bash

java \
  -Denv=db \
  -Ddb.host=localhost \
  -Ddb.port=42069 \
  -Ddb.name=jibrarian \
  -Ddb.user=jibrarian \
  -Ddb.password=password \
  -jar target/jibrarian-1.0.0-jar-with-dependencies.jar