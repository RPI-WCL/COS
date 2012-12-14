
scp build* cirrus:~/cos/
scp common/*java cirrus:~/cos/common/
scp cosManager/*java cirrus:~/cos/cosManager/
scp nodeManager/*java cirrus:~/cos/nodeManager/
scp util/*java cirrus:~/cos/util/
scp vmManager/*java cirrus:~/cos/vmManager/

ssh wclcloud@cirrus "cd /mnt/nfs/home/wclcloud/cos && ./build.sh"
