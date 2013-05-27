package cosManager;

import java.util.HashMap;

import cloudManager.CloudInfo;
import common.Constants;
import common.Controller;
import common.Message;
import common.MessageFactory;
import vmManager.VmInfo;

public class COSController extends Controller {

    HashMap<String, VmInfo> vmTable;
    HashMap<String, CloudInfo> cloudTable;
    int remainingResponses;
    bool adapting;

    public COSController() {
        super(Constants.COS_PORT);

        vmTable = new HashMap<String, VmInfo>();
        cloudTable = new HashMap<String, CloudInfo>();
        
        msgFactory = null;
        remainingResponses = 0;
        adapting = false;
    }

    public void handleMessage(Message msg) {
        System.out.println("COS RCVED " + msg.getMethod() + " from " + msg.getParam("type"));
        switch(msg.getMethod()) {
            case "cpu_usage_resp":
                handleUsageResp(msg);
                break;
            case "dropped_connection":
                handleDroppedConnection(msg);
                break;
            case "new_connection":
                handleNewConnection(msg);
                break;
            case "notify_extreme_cpu_usage":
                handleUsageResp(msg);
                break;
            case "vm_creation":
                handleVmCreation(msg);
                break;
            case "vm_destruction":
                handleVmDestruction(msg);
                break;
        }
    }

    public static void main(String[] args) throws Exception {
        COSController runner = new COSController();
        runner.checkMessages();
    }

    protected void handleUsageResp(Message msg) {

        switch((String) msg.getParam("type")) {
            case "VM":
                vmTable.get(msg.getSender()).updateUsage(msg);
                break;
            case "CLOUD":
                cloudTable.get(msg.getSender()).updateUsage(msg);
                break;
            case "NODE":
                System.err.println("Error: COS should not be directly recving usage from nodes");
                break;
        }
        remainingResponses--;
        if(remainingResponses == 0) {
            //TODO: We now have a complete snap shot of the system. Should we adapt?
            //Inset call to policy here
        }
    }


    protected void handleDroppedConnection(Message msg) {
        System.err.println("ERROR: COS currently does not handle clouds failing. Aborting...");
        System.exit(1);
    }

    protected void handleNewConnection(Message msg) {
        if(msgFactory == null) {
            msgFactory = new MessageFactory("COS", msg.getReply());
        }
        children.add(msg.getReply());
        cloudTable.put(msg.getSender(), new CloudInfo(msg.getSender(), msg.getReply()));
    }

    protected void handleExtremeUsage(Message msg) {
        if(remainingResponses == 0 && !adapting) {
            Message payload = msgFactory.getCpuUsage();
            broadcast(payload);
            remainingResponses = vmTable.size() + cloudTable.size();
        }
    }

    protected void handleVmCreation(Message msg) {
        String vm_address = (String) msg.getParam("vm_address");
        vmTable.put(vm_address, new VmInfo(vm_address, msg.getReply()));

        cloudTable.get(msg.getSender()).addVm();
        adapting = false;
    }

    protected void handleVmDestruction(Message msg) {
        String vm_address = (String) msg.getParam("vm_address");
        vmTable.remove(vm_address);

        cloudTable.get(msg.getSender()).removeVm();
        adapting = false;
    }
}
