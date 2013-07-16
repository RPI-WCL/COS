package rpiwcl.cos.cloud;

import java.util.*;
import rpiwcl.cos.common.MachInfo;
import rpiwcl.cos.util.CommChannel;


public class AmazonEC2InstanceInfo extends MachInfo {

    private int maxRuntimes;
    private HashSet<String> runtimeIds;
    private Date nextBillingTime;
    private double costPerHour;

    public AmazonEC2InstanceInfo( String address, CommChannel contact ) {
        super( address, contact );
        maxRuntimes = 0;
        runtimeIds = new HashSet<String>();
        nextBillingTime = null;
        costPerHour = 0;
    }

    public AmazonEC2InstanceInfo() {
        this( null, null );
    }


    public void setMaxRuntimes( int maxRuntimes ) {
        this.maxRuntimes = maxRuntimes;
    }

    public int getMaxRuntimes() {
        return maxRuntimes;
    }


    public HashSet<String> getRuntimeIds() {
        return runtimeIds;
    }

    public void addRuntimeId( String runtimeId ) {
        runtimeIds.add( runtimeId );
    }

    public void addRuntimeIds( HashSet<String> runtimeIds ) {
        for (String runtimeId : runtimeIds)
            runtimeIds.add( runtimeId );
    }

    public void removeRuntimeIds( HashSet<String> runtimeIds ) {
        for (String runtimeId : runtimeIds)
            runtimeIds.remove( runtimeId );
    }

    public void removeRuntimeId( String runtimeId ) {
        runtimeIds.remove( runtimeId );
    }

    public int getNumRuntimes() {
        return runtimeIds.size();
    }

    
    public void setNextBillingTime( Date nextBillingTime ) {
        this.nextBillingTime = nextBillingTime;
    }
    
    public Date getNextBillingTime() {
        return nextBillingTime;
    }

    
    public void setCostPerHour( double costPerHour ) {
        this.costPerHour = costPerHour;
    }

    public double getCostPerHour() {
        return costPerHour;
    }
    
}
