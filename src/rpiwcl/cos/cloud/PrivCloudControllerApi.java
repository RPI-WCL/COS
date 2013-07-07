package rpiwcl.cos.cloud;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import rpiwcl.cos.common.*;

public class PrivCloudControllerApi extends CloudControllerApi {

    // private HashMap<String, NodeInfo> nodeTable;
    // private HashMap<String, VmInfo> vmTable;

    public PrivCloudControllerApi( LinkedBlockingQueue<Message> mailbox, 
                                   Map cosConf, Map cloudConf, List cpumarkDb ) {
        super( mailbox, cosConf, cloudConf, cpumarkDb );
    }

    public void createRuntimes( int numRuntimes ) {}
}
