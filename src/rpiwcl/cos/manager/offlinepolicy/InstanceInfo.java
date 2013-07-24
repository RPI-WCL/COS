package rpiwcl.cos.manager.offlinepolicy;

public class InstanceInfo {
    private String type;  // public or private
    private String name;
    private int cpus;
    private double WECU;
    private double price;

    // only for public cloud
    private double ECU;

    // only for private cloud
    private String ipAddr;
    private String user;
    private int instanceLimit;


    // for private cloud
    public InstanceInfo( String type, String name, int cpus, double WECU, 
                         String ipAddr, String user, int instanceLimit ) {
        this.type = type;
        this.name = name;
        this.cpus = cpus;
        this.WECU = WECU;
        this.price = 0;
        this.ECU = (int)Math.floor( WECU );
        this.ipAddr = ipAddr;
        this.user = user;
        this.instanceLimit = instanceLimit;
    }

    // for public cloud
    public InstanceInfo( String type, String name, int cpus, double WECU, 
                         double price, double ECU ) {

        this.type = type;
        this.name = name;
        this.cpus = cpus;
        this.WECU = WECU;
        this.price = price;
        this.ECU = ECU;
        this.ipAddr = null;
        this.user = null;
        this.instanceLimit = 0;
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

    public double getWECU() {
        return WECU;
    }

    public double getPrice() {
        return price;
    }

    public double getECU() {
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
            // type=" + type +
            "name=" + name +
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

    
