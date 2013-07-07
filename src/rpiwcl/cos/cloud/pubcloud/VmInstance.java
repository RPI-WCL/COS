package rpiwcl.cos.cloud.pubcloud;

import java.util.ArrayList;

public class VmInstance {
    private String instanceId;
    private String imageId;
    private String state;
    private String privateIpAddress;
    private String publicIpAddress;

    public VmInstance() {
        this.instanceId = null;
        this.imageId = null;
        this.state = null;
        this.privateIpAddress = null;
        this.publicIpAddress = null;
    }

    public VmInstance( String instanceId, String imageId, String state,
                       String privateIpAddress, String publicIpAddress ) {
        this.instanceId = instanceId;
        this.imageId = imageId;
        this.state = state;
        this.privateIpAddress = privateIpAddress;
        this.publicIpAddress = publicIpAddress;
    }

    public String getInstanceId()       { return instanceId; }
    public String getState()            { return state; }
    public String getImageId()          { return imageId; }
    public String getPrivateIpAddress() { return privateIpAddress; }
    public String getPublicIpAddress()  { return publicIpAddress; }
    
    public void setInstanceId( String instanceId ) { this.instanceId = instanceId; }
    public void setState( String state )        { this.state = state; }
    public void setImageId( String imageId )    { this.imageId = imageId; }
    public void setPrivateIpAddress( String privateIpAddress ) { this.privateIpAddress = privateIpAddress; }
    public void setPublicIpAddress( String publicIpAddress ) { this.publicIpAddress = publicIpAddress; }

    public String toString() {
        String str = "";
        str += "instanceId: " + instanceId + ", ";
        str += "imageId: " + imageId + ", ";
        str += "state: " + state + ", ";
        str += "privateIpAddress: " + privateIpAddress + ", ";
        str += "publicIpAddress: " + publicIpAddress;
        return str;
    }

    public static ArrayList<String> getInstanceIds( ArrayList<VmInstance> instances ) {
        ArrayList<String> instanceIds = new ArrayList<String>();

        for (VmInstance instance : instances)
            instanceIds.add( instance.getInstanceId() );

        return instanceIds;
    }
}
    

