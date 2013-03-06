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
import util.CommChannel;
import util.Utility;
import util.Messages;

public class NodeController extends Controller
{
    CommChannel cos;

    List<String> theaters;

    public NodeController(String cos_addr, int cos_port, int listen_port){
        super(listen_port);


        cos = new CommChannel(cos_addr, cos_port); 
        ConnectionHandler cosHandler = new ConnectionHandler(cos, mailbox);
        new Thread(cosHandler, "COS connection").start();

        msgFactory = new MessageFactory("NODE", cos);
        theaters = null;
    }

    private CommChannel findChannelByAddress(String addr){
        for( CommChannel s : children){
            if( s.getRemoteAddr().equals(addr) ){
                return s;
            }
        }
        return null;
    }

    private void handleNewConnection(Message msg){
        children.add(msg.getReply());

        //We have a new connection. We should let COS know.
        Message payload = msgFactory.vmCreation(msg.getSender());
        cos.write(payload);

        //TODO: Tell the VM to start running a theater.
    }

    private void droppedConnection(Message msg){
        String addr = (String) msg.getParam("dropped_connection");
        Message result = msgFactory.vmDestruction(addr);
        cos.write(result);
        children.remove(msg.getReply());
    }

    private void create_vm(Message msg){
        //TODO: Should fix this warning eventually,
        theaters = (LinkedList<String>) msg.getParam("theaters");

        try{
            //Runtime.getRuntime().exec("xm create " + Constants.PATH_TO_VM_IMAGE);
            Runtime.getRuntime().exec("Terminal -x java vmManager.VMController 127.0.0.1");
        } catch(IOException e){
            e.printStackTrace();
            //TODO: Notify of failure
        }


    }

    public void handleMessage( Message msg ){
        Utility.debugPrint("Node recieved: " + msg.getMethod());

        switch(msg.getMethod())
        {
            case "get_cpu_usage":
                broadcast(msg);
                cos.write(msgFactory.cpuUsageResp());
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
                cos.write(msg);
                break;
        }
    }

    public static void main( String [] args ) throws Exception{
        if( args.length != 1)
            return;
        NodeController runner = new NodeController( args[0], Constants.COS_PORT, Constants.NODE_PORT );
        runner.checkMessages();
    }
}
