package cloudif;

import java.util.ArrayList;
import java.util.Date;
import com.amazonaws.services.ec2.model.Instance;
import cloudif.VmInstance;

public class AmazonEC2VmInstance extends VmInstance {
    private String privateDnsName;
    private String publicDnsName;
    private String instanceType;
    private Date launchTime;
    private String kernelId;
    private String architecture;
    private String rootDeviceType;
    private String rootDeviceName;
    private String virtualizationType;
    private String clientToken;
    private String hypervisor;
    private String platform; // e.g., Windows

    public AmazonEC2VmInstance() {
        super();
        this.privateDnsName = null;
        this.publicDnsName = null;
        this.instanceType = null;
        this.launchTime = null;
        this.kernelId = null;
        this.architecture = null;
        this.rootDeviceType = null;
        this.rootDeviceName = null;
        this.virtualizationType = null;
        this.clientToken = null;
        this.hypervisor = null;
        this.platform = null;
    }

    public AmazonEC2VmInstance( Instance instance ) {
        super( instance.getInstanceId(), instance.getImageId(), instance.getState().getName(),
               instance.getPrivateIpAddress(), instance.getPublicIpAddress() );

        this.privateDnsName = instance.getPrivateDnsName();
        this.publicDnsName = instance.getPublicDnsName();
        this.instanceType = instance.getInstanceType();
        this.launchTime = instance.getLaunchTime();
        this.kernelId = instance.getKernelId();
        this.architecture = instance.getArchitecture();
        this.rootDeviceType = instance.getRootDeviceType();
        this.rootDeviceName = instance.getRootDeviceName();
        this.virtualizationType = instance.getVirtualizationType();
        this.clientToken = instance.getClientToken();
        this.hypervisor = instance.getHypervisor();
        this.platform = instance.getPlatform();
    }

    public String getPrivateDnsName()   { return privateDnsName; }
    public String getPublicDnsName()    { return publicDnsName; }
    public String getInstanceType()     { return instanceType; }
    public Date getLaunchTime()         { return launchTime; }
    public String getKernelId()         { return kernelId; }
    public String getArchitecture()     { return architecture; }
    public String getRootDeviceType()   { return rootDeviceType; }
    public String getRootDeviceName()   { return rootDeviceName; }
    public String getVirtualizationType()   { return virtualizationType; }
    public String getClientToken()      { return clientToken; }
    public String getHypervisor()       { return hypervisor; }
    public String getPlatform()         { return platform; }

    public String toString() {
        String str = super.toString() + ", ";
        str += "privateDnsName: " + privateDnsName + ", ";
        str += "publicDnsName: " + publicDnsName + ", ";
        str += "instanceType: " + instanceType + ", ";
        str += "launchTime: " + launchTime + ", ";
        str += "kernelId: " + kernelId + ", ";
        str += "architecture: " + architecture + " ";
        str += "rootDeviceType: " + rootDeviceType + ", ";
        str += "rootDeviceName: " + rootDeviceName + ", ";
        str += "virtualizationType: " + virtualizationType + ", ";
        str += "clientToken: " + clientToken + ", ";
        str += "hypervisor: " + hypervisor + ", ";
        str += "platform: " + platform;
        return str;
    }
        
}
    

