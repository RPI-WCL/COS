package rpiwcl.cos.manager.offlinepolicy;

public class InstanceInfo {
    private String type;  // public or private
    private String name;
    private int cpus;
    private int ECU;

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

    public String toString() {
        String str = "[" +
            "type=" + type +
            ", name=" + name +
            // ", cpus=" + cpus +
            // ", WECU=" + WECU +
            ", price=" + price +
            ", ECU=" + ECU +
            // ", ipAddr=" + ipAddr +
            // ", user=" + user +
            // ", instanceLimit=" + instanceLimit +
            "]";
        return str;
    }

}

    
