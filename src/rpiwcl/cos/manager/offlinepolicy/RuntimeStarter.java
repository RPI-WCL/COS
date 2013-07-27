package rpiwcl.cos.manager.offlinepolicy;

import com.amazonaws.services.ec2.*;
import com.amazonaws.services.ec2.model.*;
import java.io.*;
import java.util.*;
import org.ho.yaml.Yaml;
import rpiwcl.cos.cloud.*;
import rpiwcl.cos.runtime.*;
import rpiwcl.cos.manager.*;
import rpiwcl.cos.util.*;

public class RuntimeStarter {
    private String AWS_CREDENTIAL;
    private String EC2_SECURITY_GROUP;
    private String EC2_IMAGE_ID;
    private String EC2_KEY_NAME;
    private String EC2_ZONE;

    private HashMap instanceConf;
    private HashMap runtimeConf;

    private AppRuntime appRuntime;
    private HashMap appRuntimeConf;
    private Terminal terminal;

    private AmazonEC2Api ec2Api;
    private HashMap<String, ArrayList<Instance>> ec2InstanceTable;  // key=instanceType
    private ArrayList<Instance> requestedInstances;


    public RuntimeStarter( HashMap instanceConf, HashMap runtimeConf ) {
        this.instanceConf = instanceConf;
        this.runtimeConf = runtimeConf;

        // AmazonEC2 initialization
        HashMap common = (HashMap)runtimeConf.get( "common" );
        AWS_CREDENTIAL = (String)common.get( "aws_credential" );
        EC2_SECURITY_GROUP = (String)common.get( "ec2_security_group" );
        EC2_IMAGE_ID = (String)common.get( "ec2_image_id" );
        EC2_KEY_NAME = (String)common.get( "ec2_key_name" );
        EC2_ZONE = (String)common.get( "ec2_zone" );

        ec2Api = new AmazonEC2Api( AWS_CREDENTIAL, EC2_SECURITY_GROUP );
        ec2InstanceTable = new HashMap<String, ArrayList<Instance>>();

        ArrayList<Filter> filters = new ArrayList<Filter>();
        filters.add( new Filter().withName( "tag:Purpose" ).withValues( "COS" ) );
        ArrayList<Instance> ec2Instances = ec2Api.describeInstances( filters );

        requestedInstances = new ArrayList<Instance>();

        System.out.println( "[AmazonEC2] COS instances available in Amazon EC2:" );

        for (Instance instance : ec2Instances) {
            String state = instance.getState().getName();

            if (state.equals( "running" )) {
                String instanceType = instance.getInstanceType();
                ArrayList<Instance> instanceList = ec2InstanceTable.get( instanceType );
                if (instanceList == null) {
                    instanceList = new ArrayList<Instance>();
                    ec2InstanceTable.put( instanceType, instanceList );
                }

                instanceList.add( instance );
            }
            System.out.println( instance );
        }

        // create AppRuntime and Terminal instances
        Class<?> appRuntimeClazz;
        Class<?> terminalClazz;
        try {
            appRuntimeClazz = Class.forName( (String)common.get( "app_runtime" ) );
            terminalClazz = Class.forName((String)common.get( "terminal" ));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException( e );
        }
        try {
            appRuntime = (AppRuntime)appRuntimeClazz.newInstance();
            terminal = (Terminal)terminalClazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException( e );
        } catch (IllegalAccessException e) {
            throw new RuntimeException( e );
        }        
    }


    public void startEC2Instances() {
        System.out.println( "[RuntimeStarter] startEC2Instances" );

        ArrayList ec2Confs = (ArrayList)instanceConf.get( "cloud-ec2" );
        HashMap<String, Integer> instancesToCreate = new HashMap<String, Integer>();
        
        // filter ec2InstanceTable by instanceConf
        for (int i = 0; i < ec2Confs.size(); i++) {
            HashMap ec2Conf = (HashMap)ec2Confs.get( i );
            String instanceType = (String)ec2Conf.get( "name" );
            int numRequested = ((Integer)ec2Conf.get( "num" )).intValue();

            System.out.println( "[RuntimeStarter] REQUEST: instanceType=" + instanceType +
                                ", num=" + numRequested );

            ArrayList<Instance> instanceList = ec2InstanceTable.get( instanceType );
            int numCreated = (instanceList == null) ? 0 : instanceList.size();

            if (numCreated == 0) {
                instancesToCreate.put( instanceType, new Integer( numRequested ) );
                System.out.println( "[RuntimeStarter] 0 " + instanceType +
                                    " created, creating " + numRequested );
            }
            else if (numCreated < numRequested ) {
                int numToCreate =  numRequested - numCreated;
                instancesToCreate.put( instanceType, new Integer( numToCreate ) );
                System.out.println( "[RuntimeStarter] " + numCreated + " " + 
                                    instanceType + " already created, creating " + 
                                    numToCreate + " more" );

                requestedInstances.addAll( instanceList );
            }
            else {
                // if (instanceList.size() >= numRequested)
                System.out.println( "[RuntimeStarter] " + numCreated + " " + 
                                    instanceType + " already created" );

                for (int j = 0; j < numRequested; j++) {
                    Instance instance = (Instance)instanceList.get( j );
                    requestedInstances.add( instance );
                }
            }
        }

        // create instances
        for (Iterator it = instancesToCreate.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();

            String ec2InstanceType = (String)entry.getKey();
            String ec2ImageId = EC2_IMAGE_ID;
            String ec2KeyName = EC2_KEY_NAME;
            String ec2Zone = EC2_ZONE;
            int numNewInstances = ((Integer)entry.getValue()).intValue();
            
            ArrayList<Instance> newInstanceList = 
                ec2Api.runInstances( ec2InstanceType, ec2ImageId, 
                                     ec2KeyName, ec2Zone, numNewInstances );        

            ArrayList<String> instanceIds = new ArrayList<String>();
            boolean isRunning = true;
            for (Instance instance : newInstanceList) {
                if (isRunning && !instance.getState().equals( "running" ))
                    isRunning = false;
                instanceIds.add( instance.getInstanceId() );
            }

            // block until all the instances are running
            if (!isRunning)
                ec2Api.waitForStateChange( instanceIds, "running" );
            newInstanceList = ec2Api.describeInstances( instanceIds );

            // now they are running, store information
            for (Instance instance : newInstanceList) {
                System.out.println( instance );

                String instanceType = instance.getInstanceType();
                ArrayList<Instance> instanceList = ec2InstanceTable.get( instanceType );
                if (instanceList == null) {
                    instanceList = new ArrayList<Instance>();
                    ec2InstanceTable.put( instanceType, instanceList );
                }
                instanceList.add( instance );
            }
            requestedInstances.addAll( newInstanceList );

            // stamp tags
            ArrayList<Tag> tags = new ArrayList<Tag>();
            tags.add( new Tag( "Purpose", "COS" ) );
            ec2Api.createTags( instanceIds, tags );
        }

        System.out.println( "[RuntimeStarter] requested instances: " );
        System.out.println( requestedInstances );
    }


    public void startEC2Runtimes() {
        System.out.println( "[RuntimeStarter] startEC2Runtimes" );

        HashMap ec2RuntimeConf = (HashMap)runtimeConf.get( "cloud-ec2" );
        HashMap<String, ArrayList<String>> runtimes = new HashMap<String, ArrayList<String>>();
        HashMap<String, ArrayList<String>> instanceIds = new HashMap<String, ArrayList<String>>();

        for (Instance instance : requestedInstances ) {
            ArrayList<String> runtimeList = runtimes.get( instance.getInstanceType() );
            if (runtimeList == null) {
                runtimeList = new ArrayList<String>();
                runtimes.put( instance.getInstanceType(), runtimeList );
            }
            ArrayList<String> instanceIdList = instanceIds.get( instance.getInstanceType() );
            if (instanceIdList == null) {
                instanceIdList = new ArrayList<String>();
                instanceIds.put( instance.getInstanceType(), instanceIdList );
            }

            String ipAddr = instance.getPublicIpAddress();
            ec2RuntimeConf.put( "ipaddr", ipAddr );

            // create a runtime
            RuntimeInfo runtime = appRuntime.createRuntime( ec2RuntimeConf, 4040 );
            System.out.println( "[RuntimeStarter] ipaddr=" + ipAddr + 
                                ", instanceType=" + instance.getInstanceType() );

            terminal.open( runtime.getProfile(), 
                           runtime.getTitle() + ":" + instance.getInstanceType(),
                           runtime.getUser(), runtime.getIpAddr(), 
                           runtime.getCmd(), runtime.getSshOption() );

            runtimeList.add( runtime.getId() );
            instanceIdList.add( instance.getInstanceId() );
                
            try {
                Thread.sleep( 1000 );
            } catch (Exception ex) {
                System.err.println( ex );
            }
        }

        ArrayList ec2Confs = (ArrayList)instanceConf.get( "cloud-ec2" );
        for (int i = 0; i < ec2Confs.size(); i++) {
            HashMap ec2Conf = (HashMap)ec2Confs.get( i );
            String instanceType = (String)ec2Conf.get( "name" );
            ArrayList<String> runtimeList = runtimes.get( instanceType );
            ec2Conf.put( "runtimes", runtimeList );
            ArrayList<String> instanceIdList = instanceIds.get( instanceType );
            ec2Conf.put( "instanceIds", instanceIdList );
        }
    }


    public void startRpiRuntimes() {
        System.out.println( "[RuntimeStarter] startRpiRuntimes" );

        HashMap rpiRuntimeConf = (HashMap)runtimeConf.get( "cloud-rpiwcl" );
        ArrayList rpiConfs = (ArrayList)instanceConf.get( "cloud-rpiwcl" );

        for (int i = 0; i < rpiConfs.size(); i++) {
            HashMap rpiConf = (HashMap)rpiConfs.get( i );
            String ipAddr = (String)rpiConf.get( "ipaddr" );
            rpiRuntimeConf.put( "ipaddr", ipAddr );

            // create a runtime
            RuntimeInfo runtime = appRuntime.createRuntime( rpiRuntimeConf, 4040 );
            System.out.println( "[RuntimeStarter] ipaddr=" + ipAddr );

            terminal.open( runtime.getProfile(), runtime.getTitle(), 
                           runtime.getUser(), runtime.getIpAddr(), 
                           runtime.getCmd(), runtime.getSshOption() );

            // record runtimeId on rpiConf
            ArrayList<String> runtimeList = new ArrayList<String>();
            runtimeList.add( runtime.getId() );
            rpiConf.put( "runtimes", runtimeList );
        }
    }

    
    public void writeRuntimesToFile( String filename ) {
        System.out.println( "[RuntimeStarter] writeRuntimesToFile " + filename );

        int numTasks = 0;
        int numWorkers = 0;
        ArrayList<String> output = new ArrayList<String>();

        ArrayList rpiNodes = (ArrayList)instanceConf.get( "cloud-rpiwcl" );
        for (int i = 0; i < rpiNodes.size(); i++) {
            HashMap node = (HashMap)rpiNodes.get( i );
            String name = (String)node.get( "name" );
            ArrayList workersList = (ArrayList)node.get( "workers" );
            ArrayList runtimeList = (ArrayList)node.get( "runtimes" );

            numWorkers += workersList.size();
            for (int j = 0; j < workersList.size(); j++) {
                String str = "";
                String workers = (String)workersList.get( j );
                String runtime = (String)runtimeList.get( j );

                String[] splits = workers.split( "," );
                numWorkers += splits.length;
                for (int k = 0; k < splits.length; k++) 
                    numTasks += Integer.parseInt( splits[k] );

                str += name + "," + runtime + ",null," + splits.length + "," + workers;
                output.add( str );
            }
        }

        ArrayList ec2Nodes = (ArrayList)instanceConf.get( "cloud-ec2" );
        for (int i = 0; i < ec2Nodes.size(); i++) {
            HashMap node = (HashMap)ec2Nodes.get( i );
            String name = (String)node.get( "name" );
            ArrayList workersList = (ArrayList)node.get( "workers" );
            ArrayList runtimeList = (ArrayList)node.get( "runtimes" );
            ArrayList instanceIdList = (ArrayList)node.get( "instanceIds" );

            numWorkers += workersList.size();
            for (int j = 0; j < workersList.size(); j++) {
                String str = "";
                String workers = (String)workersList.get( j );
                String runtime = (String)runtimeList.get( j );
                String instanceId = (String)instanceIdList.get( j );

                String[] splits = workers.split( "," );
                numWorkers += splits.length;
                for (int k = 0; k < splits.length; k++) 
                    numTasks += Integer.parseInt( splits[k] );

                str += name + "," + runtime + "," + instanceId +
                    " ," + splits.length + "," + workers;
                output.add( str );
            }
        }
        
        PrintWriter pw;
        File file;
        try {
            file = new File( filename );
            pw = new PrintWriter( new BufferedWriter( new FileWriter( file ) ) );

            pw.println( numWorkers + "," + numTasks );
            System.out.println( numWorkers + "," + numTasks );
            for (String str : output) {
                pw.println( str );
                System.out.println( str );
            }

            pw.close();
        } catch (IOException ex) {
            System.err.println( ex );
        }
    }

    
    public static void main( String[] args ) {
        if (args.length != 3) {
            System.err.println( 
                "Usage: java RuntimeStarter <instances file> <runtime conf file> <output>" );
        }

        HashMap instanceConf = null;
        HashMap runtimeConf = null;
        try {
            instanceConf = (HashMap)Yaml.load(new File(args[0]));
            runtimeConf = (HashMap)Yaml.load(new File(args[1]));
        } catch (FileNotFoundException ex) {
            System.err.println( "File " + args[0] + " not found" );
        }

        RuntimeStarter starter = new RuntimeStarter( instanceConf, runtimeConf );

        starter.startRpiRuntimes();

        starter.startEC2Instances();
        starter.startEC2Runtimes();

        starter.writeRuntimesToFile( args[2] );
    }
}
