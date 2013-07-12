package rpiwcl.cos.common;

import java.util.*;

import rpiwcl.cos.common.Constants;
import rpiwcl.cos.util.CommChannel;
import rpiwcl.cos.util.Utility;

public class MessageFactory
{
    private String id; //VM, NODE, COS
    private String localhost;

    public MessageFactory( String id ) {
        this.id = id; 
        this.localhost = null;
    }

    public MessageFactory(String id, CommChannel any){
        this.id = id; 
        this.localhost = any.getLocalAddr();
    }

    public Message notifyConfig( String config ) {
        Message payload = init( "notify_config" );
        payload.addParam( "config", config );
        return payload;
    }

    public Message startEntity( String id ) {
        Message payload = init( "start_entity" );
        payload.addParam( "id", id );
        return payload;
    }
    
    public Message notifyReady( Integer runtimeCapacity ) {
        Message payload = init( "notify_ready" );
        payload.addParam( "runtime_cap", runtimeCapacity );
        return payload;
    }

    public Message shutdown() {
        Message payload = init( "shutdown" );
        return payload;
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

    public Message createVm(LinkedList<String> theaters){
        Message payload = init("create_vm");
        payload.addParam("theaters", theaters);
        return payload;
    }

    public Message destroyVm(String address){
        Message payload = init("destroy_vm");
        payload.addParam("target_vm", address);
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

    public Message vmDestruction(String address){
        Message payload = init("vm_destruction");
        payload.addParam("success", true);
        payload.addParam("vm_address", address);
        return payload;
    }

    public Message startTheater(LinkedList<String> peers){
        Message payload = init("start_theater");
        payload.addParam("peers", peers);
        return payload;
    }

    public static Message newConnection(CommChannel reply){
        Message payload = new Message("new_connection", reply.getRemoteAddr());
        payload.setReply(reply);
        return payload;
    }

    public static Message droppedConnection(String addr) {
        Message payload = new Message("dropped_connection", addr);
        return payload;
    }

    private Message init(String method){
        Message payload = new Message( method, localhost);
        payload.addParam("id", id);
        return payload;
    }

}
