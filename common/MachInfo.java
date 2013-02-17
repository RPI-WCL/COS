package common;

import util.CommChannel;

public abstract class MachInfo
{
    String ipAddress;
    CommChannel contact;


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
    }

}
