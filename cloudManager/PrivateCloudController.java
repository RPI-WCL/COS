package cloudManager;

import java.io.*;
import java.lang.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import common.Controller;
import common.MachInfo;
import common.Message;
import common.MessageFactory;
import common.Constants;
import nodeManager.NodeInfo;
import util.CommChannel;
import util.Utility;
import vmManager.VmInfo;

public class PrivateCloudController extends CloudController
{

    HashMap<String, VmInfo> vmTable;
    HashMap<String, NodeInfo> nodeTable;

    int remainingResponses;
    int errorcount;
    double error;
    double prediction;
    double prev_mag;
    double prev_sum;
    boolean created;
    boolean destroyed;
    boolean vmModificationInProgress;

    public PrivateCloudController(){
        super(Constants.COS_PORT);
        vmTable = new HashMap<String, VmInfo>();
        nodeTable = new HashMap<String, NodeInfo>();
        error = 0;
        errorcount = 0;
        created = false;
        destroyed = false;
        vmModificationInProgress = false;
        remainingResponses = 0;

        //We have to wait until we get a connection to set this up.
        msgFactory = null;
    }


    public void adapt(){
        if(!VmInfo.canAdapt(vmTable.values())){
            return;
        }
        else if(vmModificationInProgress){
            return;
        }

        double magnitude = VmInfo.magnitude(vmTable.values());
        double sum = VmInfo.signedSum(vmTable.values());
        if(created || destroyed){
            if(destroyed && prev_mag < magnitude){
                    errorcount++;
            }
            if(created ){
                if(prev_mag < magnitude && prev_sum > sum){
                    errorcount++;
                }
            }
            created = false;
            destroyed = false;
        }
        error = Math.abs(prev_mag - magnitude);
        
        Message action = null;
        MachInfo target = null;
        Utility.debugPrint("Magnitude: " + Double.toString(magnitude));
        Utility.debugPrint("Predicted Destroy: " + Double.toString(VmInfo.predictDestroy(vmTable.values())));
        Utility.debugPrint("Predicted Create: " + Double.toString(VmInfo.predictCreate(vmTable.values())));
        if(magnitude - VmInfo.predictDestroy(vmTable.values()) > error * errorcount && vmTable.size() >  Constants.MIN_VMS){
            Utility.debugPrint("Trying to destroy VM");
            target = MachInfo.findMinCpu(vmTable.values());
            action = msgFactory.destroyVm(target.getAddress());
        }
        else if(magnitude - VmInfo.predictCreate(vmTable.values()) >= error * errorcount &&  vmTable.size() < Constants.MAX_VMS){
            Utility.debugPrint("Trying to create VM");
            
            //target = MachInfo.findMinCpu(nodeTable.values());
            for( NodeInfo s: nodeTable.values()) {
                if( s.vmCount() == 0) {
                    target = s;
                }
            }
            action = msgFactory.createVm(VmInfo.generateTheaters(vmTable.values()));
        }

        if(action != null){
            prev_mag = magnitude;
            vmModificationInProgress = true;
            target.getContact().write(action);
        }

    }

    public static void main(String[] args) throws Exception{
        PrivateCloudController runner = new PrivateCloudController();
        runner.checkMessages();
    }

    
    protected void handleUsageResp(Message msg){
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
        remainingResponses--;

        if(remainingResponses == 0){
            adapt();
        }

    }

    protected void handleExtremeUsage(Message msg) {
        double cpu_usage = (double) msg.getParam("load");
        String return_addr = msg.getSender();

        if(remainingResponses == 0 && !vmModificationInProgress){
            Message payload = msgFactory.getCpuUsage();
            broadcast(payload);
            remainingResponses = vmTable.size() + children.size();
        }
    }

    protected void handleNewConnection(Message msg){
        if( msgFactory == null ){
            msgFactory = new MessageFactory("COS", msg.getReply());
        }

        children.add(msg.getReply());
        nodeTable.put(msg.getSender(), new NodeInfo(msg.getSender(),  msg.getReply()));
    }


    protected void handleVmCreation(Message msg){
        String vm_address = (String) msg.getParam("vm_address");
        vmTable.put(vm_address, new VmInfo(vm_address, msg.getReply()));
        nodeTable.get(msg.getSender()).addVm();
        vmModificationInProgress = false;
    }

    protected void handleVmDestruction(Message msg){
        String vm_address = (String) msg.getParam("vm_address");
        vmTable.remove(vm_address);
        nodeTable.get(msg.getSender()).removeVm();
        vmModificationInProgress = false;
    }
}

