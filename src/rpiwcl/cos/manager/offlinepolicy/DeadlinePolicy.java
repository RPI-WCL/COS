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
    private String workerAssignmentPolicy;

    public DeadlinePolicy( double constraint, HashMap option ) {
        super( constraint );
        resConf = new ResourceConfig();
        earlyDeadlineRatio = ((Double)option.get( "early_deadline_ratio" )).doubleValue();
        workerAssignmentPolicy = (String)option.get( "worker_assignment" );

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
        System.out.println( "[Deadline] tasks=" + tasks + ", early deadline=" + newDeadline );
        double targetThroughput = (double)tasks / newDeadline;

        if (PolicyManager.useWECU) {
            targetECU = (int)Math.ceil( PolicyManager.DP_WECU_MULTI_COEFF * 
                                        targetThroughput / throughputPerECU );
        }
        else {
            // take ceiling of targetECU, otherwise targetECU cannot achieve targetThoughput
            targetECU = (int)Math.ceil(targetThroughput / throughputPerECU);
        }

        System.out.println( "[Deadline] targetThroughput=" + targetThroughput + 
                            " [tasks/sec], targetECU=" + targetECU + " [ECU]" );

        if ((privCloud != null) && schedulePrivInstances()) {
            ;// targetECU == 0;
        }
        else if (schedulePubInstances()) {
            ;// targetECU == 0;
        }
        
        System.out.println( "[Deadline] workerAssigmentPolicy=" + workerAssignmentPolicy );
        switch (workerAssignmentPolicy) {
        case "fixed-workers":
            assignFixedWorkers();
            break;
        case "variable-workers":
            assignVariableWorkers();
            break;
        }

        return resConf;
    }


    public boolean schedulePrivInstances() {
        boolean isEcuSatisfied = false;
        double totalECU = 0.0;

        for (InstanceInfo instance : privCloud) {
            targetECU = (targetECU < instance.getECU()) ? 0 : targetECU - instance.getECU();
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
                            ", remaining targetECU=" + targetECU +
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
                        // if cost is the same, then proirity is given to the latter
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


    public void assignFixedWorkers() {
        HashMap<InstanceInfo, Integer> instances = resConf.getInstances();
        int totalECU = resConf.getTotalECU();
        int totalTasks = tasks;
        int totalInstances = instances.size();

        System.out.println( "[Deadline] assignFixedWorkers, totalECU=" + totalECU + ", totalTasks=" + totalTasks );
        
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


    public void assignVariableWorkers() {
        HashMap<InstanceInfo, Integer> instances = resConf.getInstances();
        int totalECU = resConf.getTotalECU();
        int totalTasks = tasks;
        int totalInstances = instances.size();

        System.out.println( "[Deadline] assignVariableWorkers, totalECU=" + totalECU + ", totalTasks=" + totalTasks );
        
        // first, determine minimum task unit
        double minTasks = Double.MAX_VALUE;
        int totalTasksRemain = totalTasks;
        HashMap<String, Integer> tasksPerInstanceType = new HashMap<String, Integer>();

        for (Iterator it = instances.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();
            InstanceInfo instance = (InstanceInfo)entry.getKey();
            int instanceNum = ((Integer)entry.getValue()).intValue();

            int minWorkersPerInstanceType = instanceNum * instance.getCpus() * CPU_WORKER_RATIO;
            int tasksPerInstanceType_;
            if (!it.hasNext()) {
                tasksPerInstanceType_ = totalTasksRemain;
                tasksPerInstanceType.put( instance.getName(), 
                                          new Integer( tasksPerInstanceType_ ) );
            }
            else {
                tasksPerInstanceType_ = (int)(totalTasks * 
                                              (double)instanceNum * 
                                              instance.getECU() / totalECU);
                tasksPerInstanceType.put( instance.getName(), 
                                          new Integer( tasksPerInstanceType_ ) );
                totalTasksRemain -= tasksPerInstanceType_;
            }

            minTasks = Math.min( minTasks, (double)tasksPerInstanceType_ / minWorkersPerInstanceType );
            // System.out.println( "[Deadline] " + instance.getName() + 
            //                     ", minTasks=" + minTasks );
        }
        int tasksPerWorker = (int)Math.ceil( minTasks );
        System.out.println( "[Deadline] tasksPerWorker=" + tasksPerWorker );
        
        // next, assign tasks
        for (Iterator it = instances.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();
            InstanceInfo instance = (InstanceInfo)entry.getKey();
            int instanceNum = ((Integer)entry.getValue()).intValue();

            // compute taskPerInstance
            int tasksPerInstanceType_ = tasksPerInstanceType.get( instance.getName() );
            int tasksPerInstanceTypeRemain = tasksPerInstanceType_;
            int tasksPerInstance;
            for (int i = 0; i < instanceNum; i++) {
                if (i == instanceNum - 1) 
                    tasksPerInstance = tasksPerInstanceTypeRemain;
                else {
                    tasksPerInstance = tasksPerInstanceType_ / instanceNum;
                    tasksPerInstanceTypeRemain -= tasksPerInstance;
                }
                String instanceId = instance.getName() + "[" + i + "]";

                // compute workerPerInstance
                int tasksPerInstanceRemain = tasksPerInstance;
                int workersPerInstance = 
                    (int)Math.ceil( (double)tasksPerInstance / tasksPerWorker );
                ArrayList<Integer> numTasks = new ArrayList<Integer>();
                for (int j = 0; j < workersPerInstance; j++) {
                    if (j == workersPerInstance - 1)
                        numTasks.add( new Integer( tasksPerInstanceRemain ) );
                    else if ((tasksPerInstanceRemain - tasksPerWorker) <
                             (int)(0.05 * tasksPerWorker)) {
                        // if the last one is too small, add it to the previous and quit
                        numTasks.add( new Integer( tasksPerInstanceRemain ) );
                        break;
                    }
                    else {
                        numTasks.add( new Integer( tasksPerWorker ) );
                        tasksPerInstanceRemain -= tasksPerWorker;
                    }
                }
                instance.addWorkerTasks( instanceId, numTasks );
            }
        }
    }
}

    
