package rpiwcl.cos.manager.offlinepolicy;

import java.util.*;

public class InstanceInfo {
    private String type;  // public or private
    private String name;
    private int cpus;
    private int ECU;
    private HashMap<String, ArrayList<Integer>> workerTasks;

    // only for public cloud
    private double price;

    // only for private cloud
    private String ipAddr;
    private String user;
    private int instanceLimit;

    // for private cloud
    public InstanceInfo( String type, String name, int cpus, int ECU, double price,
                         String ipAddr, String user, int instanceLimit ) {
        this.type = type;
        this.name = name;
        this.cpus = cpus;
        this.ECU = ECU;
        this.price = price;
        this.ipAddr = ipAddr;
        this.user = user;
        this.instanceLimit = instanceLimit;
        this.workerTasks = new HashMap<String, ArrayList<Integer>>();
    }


    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getCpus() {
        return cpus;
    }

    public double getPrice() {
        return price;
    }

    public int getECU() {
        return ECU;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public String getUser() {
        return user;
    }

    public int getInstanceLimit() {
        return instanceLimit;
    }

    public void addWorkerTasks( String instanceId, ArrayList<Integer> numTasks ) {
        workerTasks.put( instanceId, numTasks );
    }

    public HashMap<String, ArrayList<Integer>> getWorkerTasks() {
        return workerTasks;
    }

    public String toString() {
        String str = "[" +
            "type=" + type +
            ", name=" + name +
            ", cpus=" + cpus +
            // ", WECU=" + WECU +
            ", price=" + price +
            ", ECU=" + ECU +
            // ", workerTasks=" + workerTasks +
            // ", ipAddr=" + ipAddr +
            // ", user=" + user +
            // ", instanceLimit=" + instanceLimit +
            "]";
        return str;
    }

}

    
