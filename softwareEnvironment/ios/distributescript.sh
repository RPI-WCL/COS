#!/bin/csh

#This lets you run a command on all of the nodes in nodes.txt.
#It can also verify the connection to the nodes, and it runs a few useful
#commands, ps, kill, ... as if on a single machine.

#create a list containing all urls from the nodelist file
set myname=`uname -n`
if ($1 == makelist) then
  set nodelist=`cat possiblenodes.txt | xargs echo`
  rm nodes.txt
  @ nodecnt = 0
else
  set nodelist=`cat nodes.txt | xargs echo`
endif

#go the the listed nodes and perform the specified command
foreach nodename ($nodelist)
  echo {$nodename}:
  switch ($1)
  case alive:
    #alive test
    set res=`ping -c 1 $nodename | grep "0 packets received"`
    if (! $#res) set res=`ssh $nodename uptime | grep FATAL`
    if (! $#res) then
      echo $nodename is alive
    else
      echo $nodename is unreachable
    endif
    breaksw
  case makelist:
    #use alive test to make a shorter list of working nodes
    set res=`ping -c 1 $nodename | grep "0 packets received"`
    if (! $#res) set res=`ssh $nodename uptime | grep FATAL`
    if (! $#res) then
      echo $nodename is alive
      echo $nodename >>! nodes.txt
      @ nodecnt++
    else
      echo $nodename is unreachable
    endif
    breaksw
  case ps:
    #show my processes
    ssh -x $nodename "ps -U $LOGNAME"
    breaksw
  case kill:
    #kill my named process
    ssh -x $nodename "ps -U $LOGNAME | grep $2 | cut -c 1-6 | xargs kill -9"
    breaksw
  case mkdir:
    #make this directory on all other nodes
    if ($nodename != $myname) then
      ssh -x $nodename "mkdir -p $PWD"
    endif
    breaksw
  case cp:
    #copy the specified file to the other nodes
    if ($nodename != $myname) then
      scp -r $2 {$LOGNAME}@{$nodename}:{$PWD}/{$2}
    endif
    breaksw
  default:
    #pass the command unmodified to all nodes
    ssh -f -x $nodename $*
    breaksw
  endsw
end


if ($1 == makelist) then
  echo $nodecnt nodes available
endif
