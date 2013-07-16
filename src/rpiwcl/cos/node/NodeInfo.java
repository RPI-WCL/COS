package rpiwcl.cos.node;

import java.util.*;
import rpiwcl.cos.common.MachInfo;
import rpiwcl.cos.util.CommChannel;


public class NodeInfo extends MachInfo
{
    private String cpu;
    private int cpuMark;        // retrieved from CpuDb
    private int maxRuntimes;
    private HashSet<String> runtimeIds;

    public NodeInfo(String address, CommChannel contact) {
        super(address, contact);
        runtimeIds = new HashSet<String>();
    }

    public HashSet<String> getRuntimeIds() {
        return runtimeIds;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu( String cpu ) {
        this.cpu = cpu;
    }

    public int getCpuMark() {
        return cpuMark;
    }

    public void setCpuMark( int cpuMark ) {
        this.cpuMark = cpuMark;
    }

    public int getMaxRuntimes() {
        return maxRuntimes;
    }

    public void setMaxRuntimes( int maxRuntimes ) {
        this.maxRuntimes = maxRuntimes;
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

    public static int getTotalMaxRuntimes (Collection<NodeInfo> nodes) {
        int maxRuntimes = 0;

        for (NodeInfo node : nodes)
            maxRuntimes += node.getMaxRuntimes();

        return maxRuntimes;
    }

    public static int getTotalNumRuntimes (Collection<NodeInfo> nodes) {
        int numRuntimes = 0;

        for (NodeInfo node : nodes)
            numRuntimes += node.getRuntimeIds().size();

        return numRuntimes;
    }
}
