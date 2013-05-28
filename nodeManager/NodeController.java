package nodeManager;

import java.io.*;
import java.util.*;
import java.net.Socket;
import java.lang.Thread;

import common.Controller;
import common.Constants;
import common.ConnectionHandler;
import common.MessageFactory;
import common.Message;
import common.Usage;
import util.CommChannel;
import util.Utility;
import util.Messages;

public class NodeController extends Controller
{
    CommChannel cloud;

    LinkedList<String> theaters;

    public NodeController(String cloud_addr, int cloud_port, int listen_port){
        super(listen_port);


        cloud = new CommChannel(cloud_addr, cloud_port); 
        ConnectionHandler cloudHandler = new ConnectionHandler(cloud, mailbox);
        new Thread(cloudHandler, "Cloud connection").start();

        msgFactory = new MessageFactory("NODE", cloud);
        theaters = null;
    }

    private CommChannel findChannelByAddress(String addr){
        for(CommChannel s : children){
            if(s.getRemoteAddr().equals(addr) ){
                return s;
            }
        }
        return null;
    }

    private void handleGetUsage(Message msg) {
        broadcast(msg);

        Usage usage = new Usage();
        Message resp = msgFactory.usageResponse(usage);
        cloud.write(resp);
    }

    private void handleNewConnection(Message msg){
        children.add(msg.getReply());

        //We have a new connection. We should let COS know.
        Message payload = msgFactory.vmCreation(msg.getSender());
        cloud.write(payload);

        if(theaters != null)
            msg.getReply().write(msgFactory.startTheater(theaters));
    }

    private void droppedConnection(Message msg){
        String addr = (String) msg.getParam("dropped_connection");
        Message result = msgFactory.vmDestruction(addr);
        cloud.write(result);
        children.remove(msg.getReply());
    }

    private void create_vm(Message msg){
        //TODO: Should fix this warning eventually,
        theaters = (LinkedList<String>) msg.getParam("theaters");
        try{
            Runtime.getRuntime().exec("xm create " + Constants.PATH_TO_VM_IMAGE);
            //Runtime.getRuntime().exec("Terminal -x java vmManager.VMController 127.0.0.1");
        } catch(IOException e){
            e.printStackTrace();
            //TODO: Notify of failure
        }
    }

    public void handleMessage( Message msg ){
        Utility.debugPrint("Node recieved: " + msg.getMethod());

        switch(msg.getMethod())
        {
            case "get_usage":
                handleGetUsage(msg);
                break;
            case "new_connection":
                handleNewConnection(msg);
                break;
            case "create_vm":
                create_vm(msg);
                break;
            case "destroy_vm":
                String addr = (String) msg.getParam("target_vm");
                CommChannel target = findChannelByAddress(addr);
                target.write(msg);
                break;
            case "dropped_connection":
                droppedConnection(msg);
                break;
            default:
                //If we don't know how to deal with it, pass it up.
                cloud.write(msg);
                break;
        }
    }

    public static void main( String [] args ) throws Exception{
        if( args.length != 1)
            return;
        NodeController runner = new NodeController( args[0], Constants.CLOUD_PORT, Constants.NODE_PORT );
        runner.checkMessages();
    }
}
