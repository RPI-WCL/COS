package rpiwcl.cos.manager.offlinepolicy;

import java.util.HashMap;

public class ResourceConfig {
    private HashMap<String, Integer> instances;
    private double cost;        // [USD]
    private double time;        // [sec]

    public ResourceConfig() {
        instances = new HashMap<String, Integer>();
        cost = 0.0;
        time = 0.0;
    }

    public void addInstance( InstanceInfo instance, int num ) {
        instances.put( instance.getName(), new Integer( num ) );
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

    public String toString() {
        String str = instances.toString() +
            ", cost=" + cost +
            ", time=" + time;
        return str;
    }
}
