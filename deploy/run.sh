#!/bin/bash

ZK_URL="127.0.0.1:2181"
MEDIA_HOST=$(ip addr show eth0 | grep "inet\b" | awk '{print $2}' | cut -d/ -f1)

java -jar media-server.jar --ZK_URL=$ZK_URL --MEDIA_HOST=$MEDIA_HOST