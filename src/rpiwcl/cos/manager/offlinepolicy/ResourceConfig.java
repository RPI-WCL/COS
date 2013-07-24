package rpiwcl.cos.manager.offlinepolicy;

import java.util.*;

public class ResourceConfig {
    private HashMap<InstanceInfo, Integer> instances;
    private double cost;        // [USD]
    private double time;        // [sec]

    public ResourceConfig() {
        instances = new HashMap<InstanceInfo, Integer>();
        cost = 0.0;
        time = 0.0;
    }

    public void addInstance( InstanceInfo instance, int num ) {
        Integer i = instances.get( instance );

        if (i == null) {
            instances.put( instance, new Integer( num ) );
        }
        else {
            instances.put( instance, i + 1 );
        }
    }

    public int getTotalECU() {
        int sum = 0;
        for (Iterator it = instances.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();
            InstanceInfo instance = (InstanceInfo)entry.getKey();
            Integer num = (Integer)entry.getValue();
            sum += num * instance.getECU();
        }

        return sum;
    }

    public void setCost( double cost ) {
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }

    public void setTime( double time ) {
        this.time = time;
    }

    public double getTime() {
        return time;
    }

    // public String toString() {
    //     String str = instances.toString() +
    //         ", cost=" + cost + " [USD]" +
    //         ", time=" + time + " [sec]";
    //     return str;
    // }

    public String toString() {
        String str = "";
        for (Iterator it = instances.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();
            InstanceInfo instance = (InstanceInfo)entry.getKey();
            Integer num = (Integer)entry.getValue();
            str += "instance: "  + instance.toString() + " * " + num + "\n";
        }
        str += "cost: " + cost + " [USD]" + "\n";
        str += "time: " + time + " [sec]";

        return str;
    }
}
