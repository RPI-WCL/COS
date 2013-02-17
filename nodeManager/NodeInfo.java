package nodeManager;

import common.MachInfo;
import util.CommChannel;

public class NodeInfo extends MachInfo
{
    private int vm_count;

    public NodeInfo(String address, CommChannel contact)
    {
        super(address, contact);
        vm_count = 0;
    }

    public void addVm(){
        vm_count += 1;
    }

    public void removeVm(){
        vm_count -= 1;
    }
}
