package rpiwcl.cos.manager.offlinepolicy;

import java.util.*;
import rpiwcl.cos.manager.offlinepolicy.*;


public class BudgetPolicy extends Policy {

    public BudgetPolicy( double constraint, HashMap option ) {
        super( constraint );
    }


    public ResourceConfig schedule( int tasks, double throughputPerECU,
                                    ArrayList<InstanceInfo> privCloud,
                                    ArrayList<InstanceInfo> pubCloud ) {
        return null;
    }
}

    
