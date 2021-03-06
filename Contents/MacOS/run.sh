#!/bin/sh -li

echo "run" > ~/test_app_run

cd "$(dirname $0)"
DIR=$(pwd)

export BIN_DIR=$DIR/../bin

MY_CMD=$1
if [ -z "$MY_CMD" ]; then
    MY_CMD=monitor
fi

cd $BIN_DIR
export JAVA_OPTS=-Xdock:name="JenkinsBell"

env groovy main.groovy "$MY_CMD"