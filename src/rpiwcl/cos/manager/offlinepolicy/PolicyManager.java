package rpiwcl.cos.manager.offlinepolicy;

import org.ho.yaml.Yaml;
import java.io.*;
import java.util.*;
import rpiwcl.cos.manager.offlinepolicy.*;


public class PolicyManager {
    public static final int DP_WECU_MULTI_COEFF = 100; // ECU integer for Dynamic Programming
    public static boolean useWECU = true;
    private static boolean usePrivCloud = true;

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
        useWECU = ((Boolean)config.get( "useWECU" )).booleanValue();
        usePrivCloud = ((Boolean)config.get( "usePrivCloud" )).booleanValue();

        // policy
        HashMap policyConf = (HashMap)config.get( "policy" );
        policy = PolicyFactory.getPolicy( 
            (String)policyConf.get( "type" ),
            ((Double)policyConf.get( "constraint" )).doubleValue(),
            policyConf );

        // private cloud: assuming only one
        if (usePrivCloud) {
            privCloud = new ArrayList<InstanceInfo>();
            ArrayList privInstances = (ArrayList)config.get( "cloud-rpiwcl" );
            for (int i = 0; i < privInstances.size(); i++) {
                HashMap instance = (HashMap)privInstances.get( i );
                int ecu = (useWECU) ? 
                    (int)(((Double)instance.get( "WECU" )).doubleValue() * DP_WECU_MULTI_COEFF) :
                    (int)Math.floor(((Double)instance.get( "WECU" )).doubleValue());

                InstanceInfo inst = new InstanceInfo( 
                    "private",
                    (String)instance.get( "name" ),
                    ((Integer)instance.get( "cpus" )).intValue(),
                    ecu,
                    0.0,  // price
                    (String)instance.get( "ipaddr" ),
                    (String)instance.get( "user" ),
                    ((Integer)instance.get( "instance_limit" )).intValue() );
                privCloud.add( inst );
            }
        }
        else {
            privCloud = null;
        }

        // public cloud: assuming only one
        pubCloud = new ArrayList<InstanceInfo>();
        ArrayList pubInstances = (ArrayList)config.get( "cloud-ec2" );
        for (int i = 0; i < pubInstances.size(); i++) {
            HashMap instance = (HashMap)pubInstances.get( i );
            int ecu = (useWECU) ? 
                (int)(((Double)instance.get( "WECU" )).doubleValue() * DP_WECU_MULTI_COEFF) :
                (int)((Double)instance.get( "ECU" )).doubleValue();

            InstanceInfo inst = new InstanceInfo( 
                "public",
                (String)instance.get( "name" ),
                ((Integer)instance.get( "cpus" )).intValue(),
                ecu,
                ((Double)instance.get( "price" )).doubleValue(),
                null, null, 0 );
            pubCloud.add( inst );
        }
    }


    public ResourceConfig schedule() {
        return policy.schedule( tasks, throughputPerECU, privCloud, pubCloud );
    }

    
    public static void main (String[] args) {
        if (args.length != 2) {
            System.err.println( "Usage: java PolicyManager <yaml file> <output>" );
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
        HashMap<String, ArrayList> result = resConf.writeToFile( args[1] );
        System.out.println( "[PolicyManager] Configuration result: " );
        System.out.println( result );
        
    }
}
