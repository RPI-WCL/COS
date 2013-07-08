#!/bin/bash

java rpiwcl.cos.cosmanager.EntityStarter

trap 'pkill java; exit' SIGINT

while :
  do
    sleep 10
  done
