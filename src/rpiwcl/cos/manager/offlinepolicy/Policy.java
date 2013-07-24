package rpiwcl.cos.manager.offlinepolicy;

import java.util.*;
import rpiwcl.cos.manager.offlinepolicy.*;

public abstract class Policy {
    protected int tasks;
    protected double wecu;
    protected double constraint;
    protected ArrayList<InstanceInfo> privCloud;
    protected ArrayList<InstanceInfo> pubCloud;

    public Policy( double constraint ) {
        this.constraint = constraint;
    }

    abstract ResourceConfig schedule( int tasks, double wecu, 
                                      ArrayList<InstanceInfo> privCloud,
                                      ArrayList<InstanceInfo> pubCloud );
}

    
