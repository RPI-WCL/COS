#!/bin/bash

java -DXdebug rpiwcl.cos.manager.EntityStarter

trap 'pkill java; exit' SIGINT

while :
  do
    sleep 10
  done
