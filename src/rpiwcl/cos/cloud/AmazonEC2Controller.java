package rpiwcl.cos.cloud;

import java.io.IOException;
import java.util.*;
import com.amazonaws.services.ec2.*;
import com.amazonaws.services.ec2.model.*;
import org.ho.yaml.Yaml;
import rpiwcl.cos.common.*;
import rpiwcl.cos.cloud.*;
import rpiwcl.cos.node.NodeInfo;
import rpiwcl.cos.util.*;
import rpiwcl.cos.runtime.*;
import rpiwcl.cos.vm.*;

public class AmazonEC2Controller extends Controller {
    private CommChannel starter;
    private CommChannel cos;
    private HashMap config;
    private HashMap cpuDb;

    private HashMap<String, AmazonEC2InstanceInfo> ec2InfoTable; // key=instanceId
    private HashMap<String, Instance> ec2InstanceTable;          // key=instanceId
    private AmazonEC2Api ec2Api;
    private int ec2RuntimesPerInstance;
    private String ec2InstanceType;
    private String ec2ImageId;
    private String ec2KeyName;
    private String ec2Zone;

    private AppRuntime appRuntime;
    private HashMap appRuntimeConf;


    public AmazonEC2Controller( String id, int port, String cosIpAddr, int cosPort ) {
        super( port );
        this.id = id;
        this.state = STATE_INITIALIZING;
        starter = null;
        config = null;
        cpuDb = null;

        ec2InfoTable = new HashMap<String, AmazonEC2InstanceInfo>();
        ec2InstanceTable = new HashMap<String, Instance>();
        ec2Api = null;
        ec2RuntimesPerInstance = 0;
        ec2InstanceType = null;
        ec2ImageId = null;
        ec2KeyName = null;
        ec2Zone = null;

        appRuntime = null;
        appRuntimeConf = null;

        try {
            cos = new CommChannel( cosIpAddr, cosPort );
        } catch (IOException ioe) {
            System.err.println( "[AmazonEC2] this should not happen, CosManager must be running" );
        }
        ConnectionHandler cosHandler = new ConnectionHandler( cos, mailbox );
        new Thread( cosHandler, "Cos connection" ).start();

        msgFactory = new MessageFactory( id, cos );
    }


    public void handleMessage( Message msg ) {
        System.out.println( "[AmazonEC2] Rcved " + msg.getMethod() + 
                            " from " + msg.getParam( "id" ) );

        switch( msg.getMethod() ) {
        case "new_connection":
            handleNewConnection( msg );
            break;
        case "notify_config":
            handleNotifyConfig( msg );
            notifyReady();
            break;
        case "create_runtimes":
            handleCreateRuntimes( msg );
            break;
        case "start_runtime_resp":
            handleStartRuntimeResp( msg );
            break;
        }
    }


    protected void handleNewConnection( Message msg ) {
        System.out.println( "[AmazonEC2] handleNewConnection, msg.getSender()=" + msg.getSender() );

        if (this.starter == null) {
            // if this is the first connection, it must be from EntityStarter
            this.starter = msg.getReply();
        }
        else {
            children.add(msg.getReply());
            // vmTable.put(msg.getSender(), new VmInfo(msg.getSender(), msg.getReply()));
        }
    }


    protected void handleNotifyConfig( Message msg ) {
        config = (HashMap)Yaml.load( (String)msg.getParam( "config" ) );
        Utility.debugPrint( "[AmazonEC2] config:" + config );
        cpuDb = (HashMap)config.get( "cpu_db" );
        appRuntimeConf = (HashMap)config.get( "app_runtime" );

        // create an AppRuntime instance
        HashMap common = (HashMap)config.get( "common" );
        Class<?> clazz;
        try {
            clazz = Class.forName( (String)common.get( "app_runtime" ) );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException( e );
        }
        try {
            this.appRuntime = (AppRuntime)clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException( e );
        } catch (IllegalAccessException e) {
            throw new RuntimeException( e );
        }        

        // Amazon EC2
        String credentialFile = (String)config.get( "ec2_credential_file" );
        String securityGroup = (String)config.get( "ec2_security_group" );
        ec2InstanceType = (String)config.get( "ec2_instance_type" );
        ec2ImageId = (String)config.get( "ec2_image_id" );
        ec2KeyName = (String)config.get( "ec2_key_name" );
        ec2Zone = (String)config.get( "ec2_availability_zone" );
        ec2RuntimesPerInstance = ((Integer)config.get( "ec2_runtimes_per_instance")).intValue();

        ec2Api = new AmazonEC2Api( credentialFile, securityGroup );
        ArrayList<Filter> filters = new ArrayList<Filter>();
        filters.add( new Filter().withName( "tag:Purpose" ).withValues( "COS" ) );
        ArrayList<Instance> ec2Instances = ec2Api.describeInstances( filters );

        System.out.println( "[AmazonEC2] COS instances available in Amazon EC2:" );
        for (Instance instance : ec2Instances) {
            String instanceId = instance.getInstanceId();
            ec2InstanceTable.put( instanceId, instance );

            String state = instance.getState().getName();
            if (state.equals( "running" )) {
                // register the instance on ec2InfoTable to tell it is usable
                AmazonEC2InstanceInfo ec2Info = new AmazonEC2InstanceInfo();
                ec2Info.setMaxRuntimes( ec2RuntimesPerInstance );
                ec2InfoTable.put( instanceId, ec2Info );
            }
            System.out.println( instance );
        }
    }


    private void notifyReady() {
        HashMap common = (HashMap)config.get( "common" );
        Message msg = msgFactory.notifyReady( 0, "public-cloud" );
        cos.write( msg );
        System.err.println( "[AmazonEC2] AmazonEC2Controller READY" );
        state = STATE_READY;
    }


    private void handleCreateRuntimes( Message msg ) {
        int numRuntimes = (int)msg.getParam( "num_runtimes" );

        // check if there is an available spot in running instances
        Iterator it = ec2InfoTable.keySet().iterator();
        while ((0 < numRuntimes) && it.hasNext()) {
            String instanceId = (String)it.next();
            AmazonEC2InstanceInfo ec2Info = ec2InfoTable.get( instanceId );

            int runtimeSlots = (ec2Info.getNumRuntimes() < ec2Info.getMaxRuntimes()) ?
                ec2Info.getMaxRuntimes() - ec2Info.getNumRuntimes() : 0;
            int numRequest = (runtimeSlots < numRuntimes) ? runtimeSlots : numRuntimes;
            createRuntimes( instanceId, numRequest );
            numRuntimes -= numRequest;
        }

        // if numRuntimes still remains, create new instances
        if (0 < numRuntimes) {
            int numNewInstances = (int)Math.ceil( (double)numRuntimes / ec2RuntimesPerInstance );
            ArrayList<Instance> newInstances = 
                ec2Api.runInstances( ec2InstanceType, ec2ImageId, 
                                     ec2KeyName, ec2Zone, numNewInstances );

            ArrayList<String> instanceIds = new ArrayList<String>();
            boolean isRunning = true;
            for (Instance instance : newInstances) {
                if (isRunning && !instance.getState().equals( "running" ))
                    isRunning = false;
                instanceIds.add( instance.getInstanceId() );
            }

            // block until all the instances are running
            if (!isRunning)
                ec2Api.waitForStateChange( instanceIds, "running" );
            newInstances = ec2Api.describeInstances( instanceIds );

            // now they are running, store information
            for (Instance instance : newInstances) {
                System.out.println( instance );

                String instanceId = instance.getInstanceId();
                ec2InstanceTable.put( instanceId, instance );

                AmazonEC2InstanceInfo ec2Info = new AmazonEC2InstanceInfo();
                ec2Info.setMaxRuntimes( ec2RuntimesPerInstance );
                ec2InfoTable.put( instanceId, ec2Info );

                int numRequest = (ec2Info.getMaxRuntimes() < numRuntimes) ? 
                    ec2Info.getMaxRuntimes() : numRuntimes;
                createRuntimes( instanceId, numRequest );
                numRuntimes -= numRequest;
            }

            // stamp tags
            ArrayList<Tag> tags = new ArrayList<Tag>();
            tags.add( new Tag( "Purpose", "COS" ) );
            ec2Api.createTags( instanceIds, tags );

        }
    }


    int respRemain = 0;
    HashMap<String, String> newRuntimeTable = null;
    private void createRuntimes( String instanceId, int numRuntimes ) {
        //TODO: do rsync 

        System.out.println( "[AmazonEC2] createRuntimes, numRuntimes=" + numRuntimes );
        
        // create runtimes on PM
        respRemain = numRuntimes;
        newRuntimeTable = new HashMap<String, String>();

        String ipAddr = ec2InstanceTable.get( instanceId ).getPublicIpAddress();
        appRuntimeConf.put( "ipaddr", ipAddr );
        RuntimeInfo runtime = appRuntime.createRuntime( appRuntimeConf );
        Message request = msgFactory.startRuntime( instanceId, runtime );
        starter.write( request );
    }


    public void handleStartRuntimeResp( Message msg ) {
        String instanceId = (String)msg.getParam( "host_id" );
        String runtimeId = (String)msg.getParam( "runtime_id" );
        String result =  (String)msg.getParam( "result" );
        System.out.println( "[AmazonEC2] handleStartRuntimeResp, instanceId=" + instanceId +
                            ", runtimeId=" + runtimeId + 
                            ", result=" + result );
        respRemain--;
        ec2InfoTable.get( instanceId ).addRuntimeId( runtimeId );
        String ipAddr = ec2InstanceTable.get( instanceId ).getPublicIpAddress();
        newRuntimeTable.put( runtimeId, ipAddr );

        System.out.println( "[AmazonEC2] handleCreateRuntimesResp, respRemain=" + respRemain );

        if (0 < respRemain ) {
            appRuntimeConf.put( "ipaddr", ipAddr );
            RuntimeInfo runtime = appRuntime.createRuntime( appRuntimeConf );
            Message request = msgFactory.startRuntime( instanceId, runtime );
            starter.write( request );
        }
        else {
            System.out.println( "[AmazonEC2] handleCreateRuntimesResp, sending response to CosManager: " + newRuntimeTable );
            Message resp = msgFactory.createRuntimesResp( "public-cloud", newRuntimeTable );
            cos.write( resp );
        }
    }



    public static void main( String[] args ) {
        if (4 != args.length) {
            System.err.println( "[AmazonEC2] invalid arguments" );
            System.exit( 1 );
        }
        
        // arguments: id, listen_port, parent_ipaddr, parent_port
        AmazonEC2Controller runner = new AmazonEC2Controller( 
            args[0], Integer.parseInt( args[1] ), args[2], Integer.parseInt( args[3] ) );
        runner.checkMessages();
    }

}
