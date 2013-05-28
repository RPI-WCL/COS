package cloudManager;

import common.Message;

public class AmazonCloudController extends PublicCloudController {

    public AmazonCloudController(int port) {
        super(port);
    }

    public boolean bootstrap() {
        return true;
    }

    protected void handleExtremeUsage(Message msg) {}
    protected void handleGetUsage(Message msg) {}
    protected void handleNewConnection(Message msg) {}
    protected void handleDroppedConnection(Message msg) {}
    protected void handleUsageResp(Message msg) {}
    protected void handleVmCreation(Message msg) {}
    protected void handleVmDestruction(Message msg) {}
    
}
