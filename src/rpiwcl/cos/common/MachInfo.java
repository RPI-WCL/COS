package rpiwcl.cos.common;

import java.util.Collections;
import java.util.Collection;
import java.util.Comparator;

import rpiwcl.cos.util.CommChannel;

// Information about Nodes and Virtual Machines
//
// 
public abstract class MachInfo
{
    String ipAddress;
    CommChannel contact;
    double cpuUsage;


    public MachInfo(String address, CommChannel contact){
        this.ipAddress = address;
        this.contact = contact;
    }

    public String getAddress(){
        return ipAddress;
    }

    public CommChannel getContact(){
        return contact;
    }

    public void updateCpu(double load){
       cpuUsage = load; 
    }

    public double getCpu(){
        return cpuUsage;
    }

    public static MachInfo findMinCpu(Collection<? extends MachInfo> machines){

        return Collections.min(machines, new Comparator<MachInfo>() {
            public int compare(MachInfo a, MachInfo b) {
                Double first = a.getCpu();
                return first.compareTo(b.getCpu());
            }
        });
    }

}
