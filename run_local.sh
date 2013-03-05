#!/bin/bash

Terminal -x java cosManager.COSController &
sleep 1
Terminal -x java nodeManager.NodeController 127.0.0.1 &
sleep 1

launchVM(){
Terminal -x java vmManager.VMController 127.0.0.1 &
sleep 1
}

launchVM
launchVM
launchVM

trap 'pkill java; exit' SIGINT

while :
  do
    sleep 10
  done

