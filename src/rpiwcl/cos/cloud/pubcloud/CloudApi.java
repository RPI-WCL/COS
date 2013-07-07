package rpiwcl.cos.cloud.pubcloud;

import java.util.ArrayList;
import rpiwcl.cos.cloud.pubcloud.*;


public interface CloudApi {
    public ArrayList<VmInstance> getAllInstances();
    public ArrayList<VmInstance> getInstances( ArrayList<String> instanceIds );
    public ArrayList<VmInstance> createInstances( String instanceType,
                                                  String imageId,
                                                  Integer count,
                                                  ArrayList<String> instanceIds /*OUT*/ );
    public ArrayList<VmInstanceStateChange> startInstances( ArrayList<String> instanceIds );
    public ArrayList<VmInstanceStateChange> stopInstances( ArrayList<String> instanceIds );
    public ArrayList<VmInstanceStateChange> terminateInstances( ArrayList<String> instanceIds );
}
    

