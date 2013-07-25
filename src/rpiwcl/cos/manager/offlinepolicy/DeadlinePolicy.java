package rpiwcl.cos.manager.offlinepolicy;

import java.util.*;
import rpiwcl.cos.manager.offlinepolicy.*;
import rpiwcl.cos.util.Utility;


public class DeadlinePolicy extends Policy {
    private static final int CPU_WORKER_RATIO = 2; // cpu:worker = 1:CPU_WORKER_RATIO

    private ResourceConfig resConf;
    private int targetECU;
    private double throughputPerECU;
    private double earlyDeadlineRatio;

    public DeadlinePolicy( double constraint, HashMap option ) {
        super( constraint );
        resConf = new ResourceConfig();
        earlyDeadlineRatio = ((Double)option.get( "early_deadline_ratio" )).doubleValue();

        System.out.println( "[Deadline] deadline=" + constraint + " [sec]" + 
                            ", earlyDeadlineRatio=" + earlyDeadlineRatio );
    }


    public ResourceConfig schedule( int tasks, double throughputPerECU,
                                    ArrayList<InstanceInfo> privCloud,
                                    ArrayList<InstanceInfo> pubCloud ) {
        this.tasks = tasks;
        this.throughputPerECU = throughputPerECU;
        this.privCloud = privCloud;
        this.pubCloud = pubCloud;
        
        double newDeadline = constraint * (1.0 - earlyDeadlineRatio);
        System.out.println( "[Deadline] new deadline=" + newDeadline );
        double targetThroughput = (double)tasks / newDeadline;
        targetECU = (int)Math.floor(targetThroughput / throughputPerECU);
        
        if (PolicyManager.useWECU)
            targetECU *= PolicyManager.DP_WECU_MULTI_COEFF;

        System.out.println( "[Deadline] targetThroughput=" + targetThroughput + 
                            " [tasks/sec], targetECU=" + targetECU + " [ECU]" );

        if (schedulePrivInstances()) {
            ;// targetECU == 0;
        }
        else if (schedulePubInstances()) {
            ;// targetECU == 0;
        }

        assignWorkers();

        return resConf;
    }


    public boolean schedulePrivInstances() {
        boolean isEcuSatisfied = false;
        double totalECU = 0.0;

        for (InstanceInfo instance : privCloud) {
            targetECU -= instance.getECU();
            totalECU += instance.getECU();
            resConf.addInstance( instance, 1 ); // assuming always 1 per instance

            if (targetECU <= 0)  {
                isEcuSatisfied = true;
                resConf.setCost( 0.0 );
                if (PolicyManager.useWECU)
                    resConf.setTime( tasks / 
                                     (throughputPerECU * totalECU / PolicyManager.DP_WECU_MULTI_COEFF) );
                else
                    resConf.setTime( tasks / (throughputPerECU * totalECU) );
                break;
            }
        }

        System.out.println( "[Deadline] schedulePrivInstances, isEcuSatisfied=" + isEcuSatisfied +
                            ", targetECU=" + targetECU +
                            ", resConf:" );
        System.out.println( resConf );

        return isEcuSatisfied;
    }


    public boolean schedulePubInstances() {
        double[] cost = new double[targetECU + 1];
        InstanceInfo[] minInstances = new InstanceInfo[targetECU + 1];
        int[] children = new int[targetECU + 1];
        Arrays.fill( cost, 0.0 );
        
        for (int ecu = 1; ecu <= targetECU; ecu++) {
            double q = Double.MAX_VALUE;
            Utility.debugPrint( "ecu=" + ecu + "##################" );
            
            for (InstanceInfo instance : pubCloud ) {
                Utility.debugPrint( " instance=" + instance.getName() + 
                                    ", ECU=" + instance.getECU() + 
                                    ", price=" + instance.getPrice() );

                if (ecu <= instance.getECU()) {
                    double p = instance.getPrice();
                    if (p <= q) {
                        // if cost is the same, then proirity is given latter
                        Utility.debugPrint( "  A: q=" + q + "-> p=" + p );
                        q = p;
                        minInstances[ecu] = instance;
                        children[ecu] = -1;
                    }
                }
                else {
                    int ecuRemain = ecu - instance.getECU();
                    double p = instance.getPrice() + cost[ecuRemain];
                    if (p < q) {
                        Utility.debugPrint( "  B: q=" + q + 
                                                "-> p=" + p + 
                                                "(" + instance.getPrice() + 
                                                ", cost[" + ecuRemain + "]=" + 
                                                cost[ecuRemain] + ")" );
                        q = p;
                        minInstances[ecu] = instance;
                        children[ecu] = ecuRemain;
                    }
                }
            }
            cost[ecu] = q;
            Utility.debugPrint( "cost[" + ecu + "]=" + q + 
                                ", minInstance=" + minInstances[ecu] );
            Utility.debugPrint( "" );
        }

        // backtrack arrays
        int ecu = targetECU;
        while (0 < ecu) {
            resConf.addInstance( minInstances[ecu], 1 );
            ecu = children[ecu];
        }
        resConf.setCost( cost[targetECU] );

        double totalECU = resConf.getTotalECU();
        if (PolicyManager.useWECU)
            resConf.setTime( tasks / 
                             (throughputPerECU * totalECU / PolicyManager.DP_WECU_MULTI_COEFF) );
        else
            resConf.setTime( tasks / (throughputPerECU * totalECU) );

        System.out.println( "[Deadline] schedulePubInstances, totalECU=" + totalECU +
                            ", resConf:" );
        System.out.println( resConf );
        
        return true;
    }


    public void assignWorkers() {
        HashMap<InstanceInfo, Integer> instances = resConf.getInstances();
        int totalECU = resConf.getTotalECU();
        int totalTasks = tasks;
        int totalInstances = instances.size();

        System.out.println( "[Deadline] assignWorkers, totalECU=" + totalECU + ", totalTasks=" + totalTasks );
        
        int i = 0;
        for (Iterator it = instances.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();
            InstanceInfo instance = (InstanceInfo)entry.getKey();
            int instanceNum = ((Integer)entry.getValue()).intValue();

            int workersPerInstance = instance.getCpus() * CPU_WORKER_RATIO;
            int tasksPerInstanceType = (int)(totalTasks * (double)instanceNum * instance.getECU() / totalECU);
                                             
            for (int j = 0; j < instanceNum; j++) {
                ArrayList<Integer> numTasks = new ArrayList<Integer>();

                String instanceId = instance.getName() + "[" + j + "]";
                int tasksPerInstance = tasksPerInstanceType / instanceNum;
                int tasksPerWorker = tasksPerInstance / workersPerInstance;

                // System.out.print( instanceId + ": " );

                for (int k = 0; k < workersPerInstance; k++) {
                    // if (k == workersPerInstance - 1) {
                    //     numTasks.add( new Integer( extraTasks ) );
                    // }
                    // else
                    numTasks.add( new Integer( tasksPerWorker ) );
                    instance.addWorkerTasks( instanceId, numTasks );

                }                    
                // System.out.println( numTasks );
            }
            i++;
        }
    }


    public void assignWorkers2() {
        HashMap<InstanceInfo, Integer> instances = resConf.getInstances();
        int totalECU = resConf.getTotalECU();
        int totalTasks = tasks;
        int totalTasksRemain = totalTasks;
        int totalInstances = instances.size();

        System.out.println( "totalECU=" + totalECU + ", totalTasks=" + totalTasks );
        
        int i = 0;
        for (Iterator it = instances.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();
            InstanceInfo instance = (InstanceInfo)entry.getKey();
            int instanceNum = ((Integer)entry.getValue()).intValue();

            int workersPerInstance = instance.getCpus() * CPU_WORKER_RATIO;
            int tasksPerInstanceType;
            if (i == totalInstances - 1)
                tasksPerInstanceType = totalTasks;
            else {
                tasksPerInstanceType = (int)(totalTasks * (double)instanceNum * instance.getECU() / totalECU);
                totalTasksRemain -= tasksPerInstanceType;
            }
            int tasksPerInstanceTypeRemain = tasksPerInstanceType;
            System.out.println( "instance.getName()=" + instance.getName() + 
                                ", tasksPerInstanceType=" + tasksPerInstanceType );
                                             
            for (int j = 0; j < instanceNum; j++) {
                ArrayList<Integer> numTasks = new ArrayList<Integer>();

                String instanceId = instance.getName() + "[" + j + "]";
                int tasksPerInstance = tasksPerInstanceType / instanceNum;
                int tasksPerInstanceRemain = tasksPerInstance;
                int tasksPerWorker = tasksPerInstance / workersPerInstance;

                for (int k = 0; k < workersPerInstance; k++) {
                    if ((j == instanceNum - 1) && (k == workersPerInstance - 1))
                        numTasks.add( new Integer( tasksPerInstanceTypeRemain ) );                        
                    else if (k == (workersPerInstance - 1))
                        numTasks.add( new Integer( tasksPerInstanceRemain ) );
                    else 
                        numTasks.add( new Integer( tasksPerWorker ) );
                    tasksPerInstanceRemain -= tasksPerWorker;

                    System.out.println( "instanceId=" + instanceId + 
                                        ", tasksPerWorker=" + tasksPerWorker +
                                        ", tasksPerInstanceRemain=" + tasksPerInstanceType );
                }
                tasksPerInstanceTypeRemain -= tasksPerInstance;

                System.out.println( "instance=" + instance.getName() + 
                                    ", numTasks=" + numTasks +
                                    ", taskPerInstanceTypeRemain=" + tasksPerInstanceTypeRemain );

                instance.addWorkerTasks( instanceId, numTasks );
            }

            i++;
        }
    }
    
}

    
