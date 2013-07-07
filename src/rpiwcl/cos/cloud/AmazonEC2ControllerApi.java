package rpiwcl.cos.cloud;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import rpiwcl.cos.common.*;


public class AmazonEC2ControllerApi extends CloudControllerApi {
    // private HashMap<String, VmInfo> vmTable;

    public AmazonEC2ControllerApi( LinkedBlockingQueue<Message> mailbox, 
                                   Map cosConf, Map cloudConf, List cpumarkDb ) {
        super( mailbox, cosConf, cloudConf, cpumarkDb );
    }

    public void createRuntimes(int numRuntimes) {}
}
