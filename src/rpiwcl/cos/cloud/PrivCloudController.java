package rpiwcl.cos.cloud;

import rpiwcl.cos.cloud.CloudController;
import rpiwcl.cos.node.NodeInfo;

public class PrivCloudController extends CloudController {

    public PrivCloudController(Map config);

    // sync calls
    public int getMaxRuntimesNum();
    public int getCurrentRuntimesNum();
    public List<RuntimeInfo> getRuntimesInfo();

    // async calls
    public void createRuntimes(NodeInfo node, int numRuntimes);
    public void createRuntimesResp(NodeInfo node, ArrayList<RuntimeInfo> runtimes);
    
    

}
