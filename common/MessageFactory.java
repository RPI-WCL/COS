package common;

import java.util.*;

import common.Constants;
import util.CommChannel;
import util.Utility;

public class MessageFactory
{
    String type; //VM, NODE, CLOUD, COS
    String localhost;

    public MessageFactory(String type, CommChannel any){
        this.type = type; 
        this.localhost = any.getLocalAddr();
    }

    public Message notifyExtremeUsage(Usage usage) {
        Message payload = init("notify_extreme_usage");
        payload.addParam("usage", usage);
        return payload;
    }

    public Message usageResponse(Usage usage) {
        Message payload = init("usage_response");
        payload.addParam("usage", usage);
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

    public Message getUsage(){
        Message payload = init("get_usage");
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
        payload.addParam("type", type);
        return payload;
    }

}
