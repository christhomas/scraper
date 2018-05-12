#!/usr/bin/env sh

echo "compiling"
sbt compile

echo "running $@"
exec $@ 