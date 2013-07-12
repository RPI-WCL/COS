package rpiwcl.cos.node;

import rpiwcl.cos.common.MachInfo;
import rpiwcl.cos.util.CommChannel;

public class NodeInfo extends MachInfo
{
    // private HashMap<String, VmInfo> vmTable;

    public NodeInfo(String address, CommChannel contact) {
        super(address, contact);
        // vmTable = new HashMap<String, MachInfo>();
    }
}
