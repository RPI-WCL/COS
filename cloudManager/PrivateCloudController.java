package cloudManager;

import java.io.*;
import java.lang.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import common.Controller;
import common.ConnectionHandler;
import common.MachInfo;
import common.Message;
import common.MessageFactory;
import common.Constants;
import nodeManager.NodeInfo;
import util.CommChannel;
import util.Utility;
import vmManager.VmInfo;

public class PrivateCloudController extends CloudController {

    CommChannel cos;

    HashMap<String, VmInfo> vmTable;
    HashMap<String, NodeInfo> nodeTable;

    public PrivateCloudController(String cos_addr, int cos_port, int listen_port){
        super(listen_port);

        vmTable = new HashMap<String, VmInfo>();
        nodeTable = new HashMap<String, NodeInfo>();

        cos = new CommChannel(cos_addr, cos_port);
        ConnectionHandler cosHandler = new ConnectionHandler(cos, mailbox);
        new Thread(cosHandler, "Cos connection").start();

        msgFactory = new MessageFactory("CLOUD", cos);
        
    }

    public static void main(String[] args) throws Exception{
        if(args.length != 1)
            return;
        PrivateCloudController runner = new PrivateCloudController(args[0], Constants.COS_PORT, Constants.CLOUD_PORT);
        runner.checkMessages();
    }

    
    protected void handleUsageResp(Message msg){
        System.out.println("Cloud printing here!");
        String addr = msg.getSender();
        String type = (String) msg.getParam("type");
        cos.write(msg);
        switch(type){
            case "VM":
                vmTable.get(addr).updateUsage(msg);
                break;
            case "NODE":
                nodeTable.get(addr).updateUsage(msg);
                break;
        }


    }
    protected void handleDroppedConnection(Message msg) {
    }

    protected void handleGetUsage(Message msg) {
        broadcast(msg);
    }

    protected void handleExtremeUsage(Message msg) {
        cos.write(msg);
    }

    protected void handleNewConnection(Message msg){
        children.add(msg.getReply());
        nodeTable.put(msg.getSender(), new NodeInfo(msg.getSender(),  msg.getReply()));
    }


    protected void handleVmCreation(Message msg){
        String vm_address = (String) msg.getParam("vm_address");
        vmTable.put(vm_address, new VmInfo(vm_address, msg.getReply()));
        nodeTable.get(msg.getSender()).addVm();

        cos.write(msg);
    }

    protected void handleVmDestruction(Message msg){
        String vm_address = (String) msg.getParam("vm_address");
        vmTable.remove(vm_address);
        nodeTable.get(msg.getSender()).removeVm();
    }
}

