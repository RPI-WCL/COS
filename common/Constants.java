package common;

public class Constants
{
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
    public final static String SALSA_PATH = "/home/user/salsa/salsa0.7.2.jar";
    public final static String IOS_PATH = "/home/user/ios/ios0.4.jar";
    public final static String LAUNCH_IOS = "java -cp " + SALSA_PATH + ":" + IOS_PATH +
                                            " src.testing.reachability.Full theaters.txt";

    public final static boolean DEBUG = true;
    private Constants(){}
}

