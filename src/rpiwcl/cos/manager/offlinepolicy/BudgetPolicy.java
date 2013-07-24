package rpiwcl.cos.manager.offlinepolicy;

import java.util.ArrayList;
import rpiwcl.cos.manager.offlinepolicy.*;


public class BudgetPolicy extends Policy {
    private static final int DP_MULTI_COEEF = 100; // ECU integer for Dynamic Programming

    private ResourceConfig resConf;
    private boolean useWECU;
    private int remainECU;

    public BudgetPolicy( double constraint, String option ) {
        super( constraint );
        useWECU = !option.equals( "useECU" ); // default is WECU
        resConf = new ResourceConfig();
    }


    public ResourceConfig schedule( int tasks, double throughputPerECU,
                                    ArrayList<InstanceInfo> privCloud,
                                    ArrayList<InstanceInfo> pubCloud ) {
        return null;
    }
}

    
