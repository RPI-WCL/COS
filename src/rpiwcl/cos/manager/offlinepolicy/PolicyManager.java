package rpiwcl.cos.manager.offlinepolicy;

import org.ho.yaml.Yaml;
import java.io.*;
import java.util.*;
import rpiwcl.cos.manager.offlinepolicy.*;


public class PolicyManager {
    private String appName;
    private int tasks;
    private double throughputPerECU;
    private Policy policy;

    private ArrayList<InstanceInfo> privCloud;
    private ArrayList<InstanceInfo> pubCloud;

    public PolicyManager( HashMap config ) {
        appName = (String)config.get( "app_name" );
        tasks = ((Integer)config.get( "tasks" )).intValue();
        throughputPerECU = ((Double)config.get( "throughputPerECU" )).doubleValue();

        // policy
        HashMap policyConf = (HashMap)config.get( "policy" );
        policy = PolicyFactory.getPolicy( 
            (String)policyConf.get( "type" ),
            ((Double)policyConf.get( "constraint" )).doubleValue(),
            (String)policyConf.get( "option" ) );

        // private cloud: assuming only one
        privCloud = new ArrayList<InstanceInfo>();
        ArrayList privInstances = (ArrayList)config.get( "privcloud" );
        for (int i = 0; i < privInstances.size(); i++) {
            HashMap instance = (HashMap)privInstances.get( i );
            InstanceInfo inst = new InstanceInfo( 
                "private",
                (String)instance.get( "name" ),
                ((Integer)instance.get( "cpus" )).intValue(),
                ((Double)instance.get( "WECU" )).doubleValue(),
                (String)instance.get( "ipaddr" ),
                (String)instance.get( "user" ),
                ((Integer)instance.get( "instance_limit" )).intValue() );
            privCloud.add( inst );
        }

        // public cloud: assuming only one
        pubCloud = new ArrayList<InstanceInfo>();
        ArrayList pubInstances = (ArrayList)config.get( "pubcloud" );
        for (int i = 0; i < pubInstances.size(); i++) {
            HashMap instance = (HashMap)pubInstances.get( i );
            InstanceInfo inst = new InstanceInfo( 
                "public",
                (String)instance.get( "name" ),
                ((Integer)instance.get( "cpus" )).intValue(),
                ((Double)instance.get( "WECU" )).doubleValue(),
                ((Double)instance.get( "price" )).doubleValue(),
                ((Double)instance.get( "ECU" )).doubleValue() );
            pubCloud.add( inst );
        }
    }


    public ResourceConfig schedule() {
        return policy.schedule( tasks, throughputPerECU, privCloud, pubCloud );
    }

    
    public static void main (String[] args) {
        if (args.length != 1) {
            System.err.println( "Usage: java PolicyManager <yaml file>" );
            return;
        }

        HashMap config = null;
        try {
            config = (HashMap)Yaml.load(new File(args[0]));
        } catch (FileNotFoundException ex) {
            System.err.println( "File " + args[0] + " not found" );
        }
            
        PolicyManager manager = new PolicyManager( config );
        ResourceConfig resConf = manager.schedule();
        System.out.println( resConf );
    }
}
