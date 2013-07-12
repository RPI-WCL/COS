package rpiwcl.cos.cloud;

import java.util.*;
import rpiwcl.cos.common.MachInfo;
import rpiwcl.cos.common.Message;
import rpiwcl.cos.util.CommChannel;

public class CloudInfo extends MachInfo {

    private HashMap<String, MachInfo> machTable;    // VM or node
    private LinkedList<String> runtimes;

    public CloudInfo(String address, CommChannel contact) {
        super(address, contact);
        machTable = null;
        runtimes = null;
    }

    // public void appendMachTable( HashMap<String, MachInfo> machTable ) {
    // }

    // public void setNumRuntimesInUse( int numRuntimesInUse ) {
    //     this.numRuntimesInUse = numRuntimesInUse;
    // }
    
    // public int getNumRuntimesInUse() {
    //     return numRuntimesInUse;
    // }

    // public void setNumRuntimesLimit( int numRuntimesLimit ) {
    //     this.numRuntimesLimit = numRuntimesLimit;
    // }

    // public int getNumRuntimesLimit() {
    //     return numRuntimesLimit;
    // }

}
