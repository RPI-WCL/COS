package cloudManager;

import common.Controller;
import common.Message;

public abstract class CloudController extends Controller {

    public CloudController(int port) {
        super(port);
    }

    abstract protected void handleUsageResp(Message msg);
    abstract protected void handleDroppedConnection(Message msg);
    abstract protected void handleExtremeUsage(Message msg);
    abstract protected void handleNewConnection(Message msg);
    abstract protected void handleVmCreation(Message msg);
    abstract protected void handleVmDestruction(Message msg);

    public void handleMessage(Message msg) {
        System.out.println("CLOUD RCVED " + msg.getMethod());
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
                handleExtremeUsage(msg);
                break;
            case "vm_creation":
                handleVmCreation(msg);
                break;
            case "vm_destruction":
                handleVmDestruction(msg);
                break;
            default:
                //TODO: Forward messages the CloudManager doesn't know to COS
                break;
        }
    }




}
