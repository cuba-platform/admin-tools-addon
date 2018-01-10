#!/usr/bin/env bash

TOMCAT_DIR=$1
TOMCAT_PID=$(ps ax | grep 'tomcat/bin' | grep -v grep | awk '{print $1 }')

if [[ -n $TOMCAT_PID ]]
then
    kill $TOMCAT_PID

    while [[ -n $(ps ax | grep 'tomcat/bin' | grep -v grep | awk '{print $1 }') ]]
    do
        sleep 1
    done
fi

if [[ -z $(ps ax | grep 'tomcat/bin' | grep -v grep | awk '{print $1 }') ]]
then
    $TOMCAT_DIR/bin/startup.sh
fi