package rpiwcl.cos.common;

public class Constants
{
    // Yaml files
    public final static String DEFAULT_CONFIG_FILE = "yaml/config.yaml";
    public final static String DEFAULT_CPU_DB_FILE = "yaml/cpuDb.yaml";    

    //Stastics
    public final static double HIGH_CPU = .75;
    public final static double IDEAL_CPU = .5;
    public final static double LOW_CPU = .25;

    //VM constraints
    public final static int MIN_VMS = 1;
    public final static int MAX_VMS = 1 << 1;

    //COS options
    public final static int COS_PORT = 9999;

    //Node Options
    public final static int NODE_PORT = 9998;
    public final static String PATH_TO_VM_IMAGE = "/etc/xen/cos/jcos01.cfg";

    //VM options
    public final static int VM_LOOK_BACK = 8;
    public final static String SALSA_JAR = "/home/user/salsa/salsa0.7.2.jar";
    public final static String IOS_JAR = "/home/user/ios/ios0.4.jar";
    public final static String CLASS_PATH = SALSA_JAR + ":" + IOS_JAR;
    public final static String LAUNCH_IOSTHEATER = "java -Dnetif=eth0 -cp " + CLASS_PATH + " src.IOSTheater > ios_log.txt";
    public final static String CONNECT_THEATERS = "java -Dnetif=eth0 -cp " + CLASS_PATH + " src.testing.reachability.Full theaters.txt > connect_log.txt";


    private Constants(){}
}

