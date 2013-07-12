package rpiwcl.cos.node;

import java.util.*;
import rpiwcl.cos.common.MachInfo;
import rpiwcl.cos.util.CommChannel;


public class NodeInfo extends MachInfo
{
    private HashSet<String> runtimeIds;

    public NodeInfo(String address, CommChannel contact) {
        super(address, contact);
        runtimeIds = new HashSet<String>();
    }

    public HashSet<String> getRuntimeIds() {
        return runtimeIds;
    }

    public void addRuntimeIds( HashSet<String> runtimeIds ) {
        for (String runtimeId : runtimeIds)
            runtimeIds.add( runtimeId );
    }

    public void removeRuntimeIds( HashSet<String> runtimeIds ) {
        for (String runtimeId : runtimeIds)
            runtimeIds.remove( runtimeId );
    }

    public int getNumRuntimes() {
        return runtimeIds.size();
    }

    public static int getTotalNumRuntimesLimit (Collection<NodeInfo> nodes) {
        int numRuntimesLimit = 0;

        for (NodeInfo node : nodes)
            numRuntimesLimit += node.getNumRuntimesLimit();

        return numRuntimesLimit;
    }

    public static int getTotalNumRuntimesInUse (Collection<NodeInfo> nodes) {
        int numRuntimesInUse = 0;

        for (NodeInfo node : nodes)
            numRuntimesInUse += node.getRuntimeIds().size();

        return numRuntimesInUse;
    }
}
