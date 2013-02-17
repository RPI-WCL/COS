package cosManager;

import java.io.*;
import java.lang.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import common.Controller;
import common.Message;
import common.MessageFactory;
import common.Constants;
import nodeManager.NodeInfo;
import util.CommChannel;
import util.Utility;
import vmManager.VmInfo;

public class COSController extends Controller
{

    HashMap<String, VmInfo> vmTable;
    HashMap<String, NodeInfo> nodeTable;

    final long timestep = 2 * 60;
    long timeDelay;
    boolean isWaitingResponses;
    int remaining;
    int errorcount;
    double error;
    double prediction;
    double timebackoff;
    double prev_mag;
    double prev_sum;
    boolean created;
    boolean destroyed;
    boolean lock;

    public COSController(){
        super(Constants.COS_PORT);
        vmTable = new HashMap<String, VmInfo>();
        nodeTable = new HashMap<String, NodeInfo>();
        timeDelay = 0;
        error = 0;
        timebackoff = 1;
        errorcount = 0;
        isWaitingResponses = false;
        created = false;
        destroyed = false;
        lock = false;

        //We have to wait until we get a connection to set this up.
        msgFactory = null;
    }


    public void adapt(){
//        if( !ConStat.canChange(vmList) ){
//            System.out.println("Can't do shit.");
//            return;
//        }
//
//
//        long next = System.currentTimeMillis();
//        double magnitude = ConStat.magnitude(vmList);
//        double sum = ConStat.signedSum(vmList);
//        if(created || destroyed){
//            if(destroyed && prev_mag < magnitude ){
//                    errorcount++;
//            }
//            if( created ){
//                if( prev_mag < magnitude && prev_sum  > sum ){
//                    errorcount++;
//                }
//            }
//            created = false;
//            destroyed = false;
//        }
//        error = Math.abs(prev_mag - magnitude);
//        
//
//        if( magnitude - ConStat.predictDestroy(vmList) > error * errorcount / timebackoff  && vmList.size() -1 > minVm){
//            prev_mag = magnitude;
//            System.out.println("Destroy!");
//            String key = ConStat.findMinVm(vmList);
//            ConStat alpha = vmList.get(key);
//            String msg = msgHandler.destroy_vm_request(key);
//            alpha.getParent().write(msg);
//            timeDelay = System.currentTimeMillis();
//            lock = true;
//        }
//        else if( magnitude - ConStat.predictCreate(vmList) > error * errorcount / timebackoff &&  vmList.size() < maxVm ){
//            prev_mag = magnitude;
//            System.out.println("CREATE!");
//            CommChannel node = ConStat.findMinNode(socketStats);
//            String msg = msgHandler.create_vm_request(vmCfgFile, new LinkedList<String>());
//            node.write(msg);
//            timeDelay = System.currentTimeMillis();
//            lock = true;
//        }
    }

    public void handleMessage( Message msg )
    {
        double cpu_usage;
        String return_addr;
        List<String> params;
        Message payload;

        Utility.debugPrint("COS recieved: " + msg.getMethod());
        switch(msg.getMethod())
        {
            case "cpu_usage_resp":
                updateCpuStats(msg);
                break;
            case "new_connection":
                newNode(msg);
                break;
            case "notify_extreme_cpu_usage":
                cpu_usage = (double) msg.getParam("load");
                return_addr = msg.getSender();

                if( !isWaitingResponses ){
                    payload = msgFactory.getCpuUsage();
                    broadcast(payload);
                    isWaitingResponses = true;
                    remaining = vmTable.size() + children.size();
                }
                break;
            case "vm_creation":
                newVM(msg);
                break;
            case "vm_destruction":
                deleteVM(msg);
                break;
            default:
                System.out.println(msg.getMethod());
                break;
        }
    }

    public static void main(String[] args) throws Exception{
        COSController runner = new COSController();
        runner.checkMessages();
    }

    private void updateCpuStats(Message msg){
        double load = (double) msg.getParam("load");
        String addr = msg.getSender();
        String type = (String) msg.getParam("type");
        switch(type){
            case "VM":
                vmTable.get(addr).updateCpu(load);
                break;
            case "NODE":
                nodeTable.get(addr).updateCpu(load);
                break;
        }
        remaining--;

        if(remaining == 0){
            isWaitingResponses = false;
            adapt();
        }

    }

    private void newNode(Message msg){
        if( msgFactory == null ){
            msgFactory = new MessageFactory("COS", msg.getReply());
        }

        children.add(msg.getReply());
        nodeTable.put(msg.getSender(), new NodeInfo(msg.getSender(),  msg.getReply()));
    }


    private void newVM(Message msg){
        String vm_address = (String) msg.getParam("vm_address");
        vmTable.put(vm_address, new VmInfo(vm_address, msg.getReply()));
        nodeTable.get(msg.getSender()).addVm();
    }

    private void deleteVM(Message msg){
        String vm_address = (String) msg.getParam("vm_address");
        vmTable.remove(vm_address);
        nodeTable.get(msg.getSender()).removeVm();
    }
}

