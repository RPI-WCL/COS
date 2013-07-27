package rpiwcl.cos.manager.offlinepolicy;

import org.ho.yaml.Yaml;
import java.io.*;
import java.util.*;

public class ResourceConfig {
    private HashMap<InstanceInfo, Integer> instances;
    private double cost;        // [USD]
    private double time;        // [sec]

    public ResourceConfig() {
        instances = new HashMap<InstanceInfo, Integer>();
        cost = 0.0;
        time = 0.0;
    }

    public HashMap<InstanceInfo, Integer> getInstances() {
        return instances;
    }

    public void setInstances( HashMap<InstanceInfo, Integer> instances) {
        this.instances = instances;
    }

    public void addInstance( InstanceInfo instance, int num ) {
        Integer i = instances.get( instance );

        if (i == null) {
            instances.put( instance, new Integer( num ) );
        }
        else {
            instances.put( instance, i + 1 );
        }
    }

    public int getTotalECU() {
        int sum = 0;
        for (Iterator it = instances.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();
            InstanceInfo instance = (InstanceInfo)entry.getKey();
            Integer num = (Integer)entry.getValue();
            sum += num * instance.getECU();
        }

        return sum;
    }

    public void setCost( double cost ) {
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }

    public void setTime( double time ) {
        this.time = time;
    }

    public double getTime() {
        return time;
    }

    // public String toString() {
    //     String str = instances.toString() +
    //         ", cost=" + cost + " [USD]" +
    //         ", time=" + time + " [sec]";
    //     return str;
    // }

    public String toString() {
        String str = "";
        for (Iterator it = instances.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();
            InstanceInfo instance = (InstanceInfo)entry.getKey();
            Integer num = (Integer)entry.getValue();
            str += "instance: "  + instance.toString() + " * " + num + "\n";
        }
        str += "cost: " + cost + " [USD]" + "\n";
        str += "time: " + time + " [sec]";

        return str;
    }

    
    public HashMap<String, ArrayList> writeToFile( String filename ) {
        HashMap<String, ArrayList> clouds = new HashMap<String, ArrayList>();
        ArrayList<HashMap> privCloud = new ArrayList<HashMap>();
        ArrayList<HashMap> pubCloud = new ArrayList<HashMap>();
        clouds.put( "cloud-rpiwcl", privCloud );
        clouds.put( "cloud-ec2", pubCloud );
        
        for (Iterator it = instances.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();
            InstanceInfo instance = (InstanceInfo)entry.getKey();
            int instanceNum = ((Integer)entry.getValue()).intValue();

            HashMap instance_ = new HashMap();
            if (instance.getType().equals( "private" )) {
                privCloud.add( instance_ );
                instance_.put( "user", instance.getUser() );
                instance_.put( "ipaddr", instance.getIpAddr() );
            }
            else {
                pubCloud.add( instance_ );
                instance_.put( "price", new Double( instance.getPrice() ) );
            }

            instance_.put( "cpus", new Integer( instance.getCpus() ) );
            instance_.put( "name", instance.getName() );
            instance_.put( "ECU", new Integer( instance.getECU() ) );
            instance_.put( "num", new Integer( instanceNum ) );
            ArrayList<String> workerTasks_ = new ArrayList<String>();
            instance_.put( "workers", workerTasks_ );

            HashMap<String, ArrayList<Integer>> workerTasks = instance.getWorkerTasks();
            for (Iterator it_ = workerTasks.entrySet().iterator(); it_.hasNext(); ) {
                String str = "";
                Map.Entry entry_ = (Map.Entry)it_.next();
                ArrayList<Integer> tasks = (ArrayList<Integer>)entry_.getValue();
                for (int i = 0; i < tasks.size(); i++) {
                    Integer task = (Integer)tasks.get( i );
                    if (i < tasks.size() - 1)
                        str += task + ",";
                    else
                        str += task;
                }
                workerTasks_.add( str );
            }
        }

        try {
            Yaml yaml = new Yaml();
            yaml.dump( clouds, new File( filename ) );
        } catch (IOException ex) {
            System.err.println( "File not found: " + filename );
        }

        return clouds;
    }
}
