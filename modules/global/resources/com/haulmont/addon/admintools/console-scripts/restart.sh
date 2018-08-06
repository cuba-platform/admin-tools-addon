#!/usr/bin/env bash

TOMCAT_DIR="$1"
ps -ef | grep tomcat/bin | awk '{print $2}' | xargs kill -9
"$TOMCAT_DIR"/bin/startup.sh