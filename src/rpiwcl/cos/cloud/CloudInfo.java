package rpiwcl.cos.cloud;

import java.util.*;
import rpiwcl.cos.common.MachInfo;
import rpiwcl.cos.common.Message;
import rpiwcl.cos.util.CommChannel;

public class CloudInfo extends MachInfo {
    private String type;
    private int maxRuntimes;
    private HashMap<String, String> runtimeTable;   // runtimeId, hostIpAddr

    public CloudInfo(String address, CommChannel contact) {
        super(address, contact);
        type = null;
        runtimeTable = new HashMap<String, String>();
    }

    
    public void setType( String type ) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public int getMaxRuntimes() {
        return maxRuntimes;
    }

    public void setMaxRuntimes( int maxRuntimes ) {
        this.maxRuntimes = maxRuntimes;
    }


    public HashMap<String, String> getRuntimeTable() {
        return runtimeTable;
    }

    public void updateRuntimeTable( HashMap<String, String> runtimeTable ) {
        this.runtimeTable.putAll( runtimeTable );
        System.out.println( "updateRuntimeTable, runtimeTable=" + this.runtimeTable );
    }

    public int getNumRuntimes() {
        return runtimeTable.size();
    }

    public static int getTotalMaxRuntimes (Collection<CloudInfo> clouds) {
        int maxRuntimes = 0;

        for (CloudInfo cloud : clouds)
            maxRuntimes += cloud.getMaxRuntimes();

        return maxRuntimes;
    }

    public static int getTotalNumRuntimes (Collection<CloudInfo> clouds) {
        int numRuntimes = 0;

        for (CloudInfo cloud : clouds)
            numRuntimes += cloud.getRuntimeTable().size();

        return numRuntimes;
    }
}
