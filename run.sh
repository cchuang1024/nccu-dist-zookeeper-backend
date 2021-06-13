#!/bin/bash

ZK_URL="127.0.0.1:2181"
MEDIA_HOST="127.0.0.1"

java -jar media-server.jar --ZK_URL=$ZK_URL --MEDIA_HOST=$MEDIA_HOST