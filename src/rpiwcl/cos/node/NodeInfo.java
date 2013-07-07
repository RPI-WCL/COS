package rpiwcl.cos.node;

import rpiwcl.cos.common.MachInfo;
import rpiwcl.cos.util.CommChannel;

public class NodeInfo extends MachInfo
{
    private int vm_count;

    public NodeInfo(String address, CommChannel contact) {
        super(address, contact);
        vm_count = 0;
    }

    public void addVm(){
        vm_count += 1;
    }

    public int vmCount() {
        return vm_count;
    }

    public void removeVm(){
        vm_count -= 1;
    }
}
