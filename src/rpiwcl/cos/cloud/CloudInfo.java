package rpiwcl.cos.cloud;

import java.util.*;
import rpiwcl.cos.common.MachInfo;
import rpiwcl.cos.common.Message;
import rpiwcl.cos.util.CommChannel;

public class CloudInfo extends MachInfo {
    String type;   // private or public
    private HashMap<String, String> runtimeTable;   // runtimeId, hostIpAddr

    public CloudInfo(String address, CommChannel contact) {
        super(address, contact);
        type = null;
        runtimeTable = new HashMap<String, String>();
    }

    public HashMap<String, String> getRuntimeTable() {
        return runtimeTable;
    }

    public void updateRuntimeTable( HashMap<String, String> runtimeTable ) {
        this.runtimeTable.putAll( runtimeTable );
        System.out.println( "updateRuntimeTable, runtimeTable=" + this.runtimeTable );
    }

    public void setType( String type ) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    public int getNumRuntimes() {
        return runtimeTable.size();
    }

    public static int getTotalNumRuntimesLimit (Collection<CloudInfo> clouds) {
        int numRuntimesLimit = 0;

        for (CloudInfo cloud : clouds)
            numRuntimesLimit += cloud.getNumRuntimesLimit();

        return numRuntimesLimit;
    }

    public static int getTotalNumRuntimesInUse (Collection<CloudInfo> clouds) {
        int numRuntimesInUse = 0;

        for (CloudInfo cloud : clouds)
            numRuntimesInUse += cloud.getRuntimeTable().size();

        return numRuntimesInUse;
    }
}
