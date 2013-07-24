package rpiwcl.cos.manager.offlinepolicy;

import java.util.*;
import rpiwcl.cos.manager.offlinepolicy.*;
import rpiwcl.cos.util.Utility;


public class DeadlinePolicy extends Policy {
    private static final int DP_MULTI_COEFF = 100; // ECU integer for Dynamic Programming

    private ResourceConfig resConf;
    private boolean useWECU;
    private int remainECU;
    private double throughputPerECU;

    public DeadlinePolicy( double constraint, String option ) {
        super( constraint );
        useWECU = !option.equals( "useECU" ); // default is WECU
        resConf = new ResourceConfig();
    }


    public ResourceConfig schedule( int tasks, double throughputPerECU,
                                    ArrayList<InstanceInfo> privCloud,
                                    ArrayList<InstanceInfo> pubCloud ) {
        this.tasks = tasks;
        this.throughputPerECU = throughputPerECU;
        this.privCloud = privCloud;
        this.pubCloud = pubCloud;

        double throughputTarget = (double)tasks / constraint;
        remainECU = (int)Math.ceil(throughputPerECU / throughputTarget);
        if (useWECU)
            remainECU *= DP_MULTI_COEFF;

        if (schedulePrivInstances()) {
            ;// remainECU == 0;
        }
        else if (schedulePubInstances()) {
            ;// remainECU == 0;
        }

        return resConf;
    }


    public boolean schedulePrivInstances() {
        boolean isEcuSatisfied = false;
        double sumECU = 0.0;

        for (InstanceInfo instance : privCloud) {
            remainECU -= getECU( instance );
            sumECU += getECU( instance );
            resConf.addInstance( instance, 1 ); // assuming always 1 per instance

            if (remainECU <= 0)  {
                isEcuSatisfied = true;
                resConf.setCost( 0.0 );
                if (!useWECU)
                    resConf.setTime( tasks / (sumECU * DP_MULTI_COEFF) );
                else
                    resConf.setTime( tasks / sumECU );
                break;
            }
        }

        System.out.println( "[Deadline] schedulePrivInstances, isEcuSatisfied=" + isEcuSatisfied +
                            ", resConf=" + resConf + 
                            ", remainECU=" + remainECU );

        return isEcuSatisfied;
    }


    public boolean schedulePubInstances() {
        int TARGET_ECU = remainECU;
        double[] cost = new double[TARGET_ECU + 1];
        InstanceInfo[] minInstances = new InstanceInfo[TARGET_ECU + 1];
        Arrays.fill( cost, 0.0 );
        
        for (int ecu = 1; ecu <= TARGET_ECU; ecu++) {
            double q = Double.MAX_VALUE;
            Utility.debugPrint( "ecu=" + ecu + "##################" );
            
            for (InstanceInfo instance : pubCloud ) {
                Utility.debugPrint( " instance=" + instance.getName() + 
                                    ", ECU=" + getECU( instance ) +
                                    ", price=" + instance.getPrice() );

                if (ecu <= getECU( instance )) {
                    double p = instance.getPrice();
                    if (p <= q) {
                        // if cost is the same, then proirity is given latter
                        Utility.debugPrint( "  A: q=" + q + "-> p=" + p );
                        q = p;
                        minInstances[ecu] = instance;
                    }
                }
                else {
                    int ecuRemain = ecu - getECU( instance );
                    double p = instance.getPrice() + cost[ecuRemain];
                    if (p < q) {
                        Utility.debugPrint( "  B: q=" + q + 
                                                "-> p=" + p + 
                                                "(" + instance.getPrice() + 
                                                ", cost[" + ecuRemain + "]=" + 
                                                cost[ecuRemain] + ")" );
                        q = p;
                        minInstances[ecu] = instance;
                    }
                }
            }
            cost[ecu] = q;
            Utility.debugPrint( "cost[" + ecu + "]=" + q + 
                                ", minInstance=" + minInstances[ecu] );
            Utility.debugPrint( "" );
        }
                        
        return true;
    }


    // public boolean schedulePubInstances() {
    //     int TARGET_ECU = remainECU;
    //     double[] cost = new double[TARGET_ECU + 1];
    //     HashMap[] minConfig = new HashMap[TARGET_ECU + 1];
    //     Arrays.fill( cost, 0.0 );
        
    //     for (int ecu = 1; ecu <= TARGET_ECU; ecu++) {
    //         double q = Double.MAX_VALUE;
    //         minConfig[ecu] = new HashMap<InstanceInfo, Integer>();
    //         System.out.println( "ecu=" + ecu + "##################" );
            
    //         for (InstanceInfo instance : pubCloud ) {
    //             int k = (int)Math.ceil( (double)ecu / getECU( instance ) );

    //             System.out.println( " instance=" + instance.getName() + 
    //                                 ", ECU=" + instance.getECU() +
    //                                 ", price=" + instance.getPrice() +
    //                                 " (k=" + k + ")");

    //             for (int j = 1; j <= k; j++) {
    //                 if (j == k) {
    //                     double p = instance.getPrice() * k;
    //                     if (instance.getPrice() * k < q) {
    //                         System.out.println( "  A: k=" + k +
    //                                             ", q=" + q + 
    //                                             "-> p=" + p + 
    //                                             "(" + instance.getPrice() + "*" + k + ")" );
    //                         q = p;
    //                         minConfig[ecu].put( instance, new Integer( k ) );
    //                     }
    //                 }
    //                 else {
    //                     int ecuRemain = ecu - getECU( instance ) * j;
    //                     double p = instance.getPrice() * j + cost[ecuRemain];
    //                     if (p < q) {
    //                         System.out.println( "  B: j=" + j +
    //                                             ", q=" + q + 
    //                                             "-> p=" + p + 
    //                                             "(" + instance.getPrice() + "*" + j + 
    //                                             ", cost[" + ecuRemain + "]=" + 
    //                                             cost[ecuRemain] + ")" );
    //                         q = p;
    //                         minConfig[ecu].put( instance, new Integer( j ) );
    //                     }
    //                 }
    //             }
    //         }
    //         cost[ecu] = q;
    //         System.out.println( "cost[" + ecu + "]=" + q + 
    //                             ", minConfig=" + minConfig[ecu] );
    //         System.out.println();
    //     }
                        
    //     return true;
    // }


    private int getECU( InstanceInfo instance ) {
        double value = (useWECU) ? instance.getWECU() : instance.getECU();
        if (useWECU)
            value *= DP_MULTI_COEFF;
        return (int)value;
    }
}

    
