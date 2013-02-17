package common;

import common.Constants;
import util.CommChannel;
import util.Utility;

public class MessageFactory
{
    String type; //VM, NODE, COS
    String localhost;

    public MessageFactory(String type, CommChannel any){
        this.type = type; 
        this.localhost = any.getLocalAddr();
    }

    public Message notifyCpuUsage(Double load){
        Message payload = init( "notify_extreme_cpu_usage");
        payload.addParam("load", load);
        return payload;
    }

    public Message cpuUsageResp(Double load){
        Message payload = init("cpu_usage_resp");
        payload.addParam("load", load);
        return payload;
    }

    public Message cpuUsageResp(){
        double load = Utility.getWeightedSystemLoadAverage();
        return this.cpuUsageResp(load);
    }

    public Message getCpuUsage(){
        Message payload = init("get_cpu_usage");
        return payload;
    }

    public Message vmCreation(String address){
        Message payload = init("vm_creation");
        payload.addParam("success", true);
        payload.addParam("vm_address", address);
        return payload;
    }

    public static Message newConnection(CommChannel reply){
        Message payload = new Message("new_connection", reply.getRemoteAddr());
        payload.setReply(reply);
        return payload;
    }

    private Message init(String method){
        Message payload = new Message( method, localhost);
        payload.addParam("type", type);
        return payload;
    }

}
