package cloudManager;

import common.MachInfo;
import common.Message;
import util.CommChannel;

public class CloudInfo extends MachInfo {

    int vm_count;

    public CloudInfo(String address, CommChannel contact) {
        super(address, contact);
        vm_count = 0;
    }

    @Override
    public void updateUsage(Message msg) {
        //TODO: Clouds usage contains stats for all their Nodes. Must use a list of something.
    }

    public void addVm(){
        vm_count += 1;
    }

    public int vmCount() {
        return vm_count;
    }

    public void removeVm() {
        vm_count -= 1;
    }

}
