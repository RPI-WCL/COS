package rpiwcl.cos.manager;

import java.text.ParseException;
import java.util.*;
import org.ho.yaml.Yaml;
import rpiwcl.cos.cloud.*;
import rpiwcl.cos.common.*;
import rpiwcl.cos.util.*;
import rpiwcl.cos.manager.policy.*;


public class CosManager extends Controller {
    private String id;
    private MessageFactory msgFactory;
    private Policy policy;
    private ThroughputPredictor tpPredictor;
    private CommChannel starter;
    private boolean reconfiguration;
    private HashMap config;
    
    private int initialNumRuntimes;
    private int privNumRuntimes;
    private int pubNumRuntimes;

    private HashMap<String, CloudInfo> privCloudTable;
    private ArrayList privClouds;
    private HashMap<String, CloudInfo> pubCloudTable;
    private ArrayList pubClouds;


    public CosManager( String id, int port ) {
        super( port );
        this.id = id;
        this.state = STATE_INITIALIZING;
        msgFactory = null;
        tpPredictor = new ThroughputPredictor( ThroughputPredictor.POLICY_NO_LIMIT, -1 );
        starter = null;
        reconfiguration = false;
        config = null;
        initialNumRuntimes = 0;
        privNumRuntimes = 0;
        pubNumRuntimes = 0;
        privCloudTable = new HashMap<String, CloudInfo>();
        privClouds = null;
        pubCloudTable = new HashMap<String, CloudInfo>();
        pubClouds = null;
    }

    public void handleMessage( Message msg ) {
        System.out.println( "[CosManager] Rcved " + msg.getMethod() +
                            " from " + msg.getParam( "id" ) );

        switch( msg.getMethod() ) {
        case "new_connection":
            handleNewConnection( msg );
            break;
        case "notify_config":
            handleNotifyConfig( msg );
            break;
        case "notify_ready":
            handleNotifyReady( msg );
            break;
        case "create_runtimes_resp":    // from {PrivCloud, AmazonCloud}Controller
            handleCreateRuntimesResp( msg );
            break;

        // from application
        case "cosif_open":
            handleCosIfOpen( msg );
            break;
        case "cosif_report_num_tasks":
            handleCosIfReportNumTasks( msg );
            break;
        case "cosif_register_workers":
            handleCosIfRegisterWorkers( msg );
            break;
        case "cosif_report_progress":
            handleCosIfReportProgress( msg );
            break;
        case "cosif_close":
            handleCosIfClose( msg );
            break;
        }
    }

    private void handleNewConnection( Message msg ) {
        if (this.msgFactory == null) {
            this.msgFactory = new MessageFactory( id, msg.getReply() );
        }

        if (this.starter == null) {
            // if this is the first connection, it must be from EntityStarter
            this.starter = msg.getReply();
        }
        else {
            children.add( msg.getReply() );

        }
    }

    private void handleNotifyConfig( Message msg ) {
        config = (HashMap)Yaml.load( (String)msg.getParam( "config" ) );
        Utility.debugPrint( "[CosManager] config:" + config );

        try {
            this.policy = PolicyFactory.getPolicy( (String)config.get( "policy" ) );
        } catch (ParseException pex) {
            System.err.println( pex );
        }

        this.reconfiguration = ((Boolean)config.get( "reconfiguration" )).booleanValue();
        this.initialNumRuntimes = ((Integer)config.get( "initial_num_runtimes" )).intValue();
        
        // start private clouds
        privClouds = (ArrayList)config.get( "private_clouds" );
        for (int i = 0; i < privClouds.size(); i++) {
            String cloud = (String)privClouds.get( i );
            msg = msgFactory.startEntity( cloud );
            starter.write( msg );
        }

        // start public clouds
        pubClouds = (ArrayList)config.get( "public_clouds" );
        for (int i = 0; i < pubClouds.size(); i++) {
            String cloud = (String)pubClouds.get( i );
            msg = msgFactory.startEntity( cloud );
            starter.write( msg );
        }
    }

    private int readyReceived = 0;
    private void handleNotifyReady( Message msg ) {
        int maxRuntimes = (int)msg.getParam( "max_runtimes" );
        String type = (String)msg.getParam( "type" );

        CloudInfo cloud = new CloudInfo( msg.getSender(), msg.getReply() );
        cloud.setMaxRuntimes( maxRuntimes );
        cloud.setType( type );
        boolean flag = false;

        if (type.equals( "private-cloud" )) {
            privCloudTable.put( msg.getSender(), cloud );
            flag = true;            
        } 
        else if (type.equals( "public-cloud" )) {
            pubCloudTable.put( msg.getSender(), cloud );
            flag = true;
        }

        if (!flag) {
            System.err.println( "[CosManager] handleNotifyReady, invalid type=" + type );
            return;
        }

        readyReceived++;

        if ((state == STATE_INITIALIZING) && 
            ((privClouds.size() + pubClouds.size()) == readyReceived)) {
            System.out.println( "[CosManager] READY received from all clouds, maxRuntimes="
                                + maxRuntimes );
            
            // createPrivRuntimes( initialNumRuntimes );
            createPubRuntimes( initialNumRuntimes );
        }
        else if (state == STATE_READY) {
            //TODO: new resource is added, notify CosManager the new maxRuntimes
            System.out.println( "[CosManager] TODO: new resource is added" );
        }
    }


    private boolean createPrivRuntimes( int numRuntimes ) {
        System.out.println( "[CosManager] createPrivRuntimes, numRuntimes=" + numRuntimes );

        int totalMaxRuntimes = CloudInfo.getTotalMaxRuntimes( privCloudTable.values() );
        int totalNumRuntimes = CloudInfo.getTotalNumRuntimes( privCloudTable.values() );

        if ((totalMaxRuntimes - totalNumRuntimes) < numRuntimes) {
            System.err.println( "[CosManager] no enough room to create " + 
                                numRuntimes + " runtimes (=" + 
                                totalMaxRuntimes + ", =" +
                                totalNumRuntimes + ")" );
            return false;
        }
        else {
            Collection<CloudInfo> clouds = privCloudTable.values();
            int numRemain = numRuntimes;
            for (CloudInfo cloud : clouds) {
                int numNot = cloud.getMaxRuntimes() - cloud.getNumRuntimes();
                int numRequest =  (numNot < numRemain) ? numRemain - numNot : numRemain;

                CommChannel contact = cloud.getContact();
                Message msg = msgFactory.createRuntimes( numRequest );
                contact.write( msg );

                System.out.println( "[CosManager] createPrivRuntimes, (contact=" + 
                                    contact + ", numRequest=" + numRequest );
                
                numRemain -= numRequest;
                if (numRemain == 0)
                    break;
            }
        }

        return true;
    }


    private void createPubRuntimes( int numRuntimes ) {
        System.out.println( "[CosManager] createPubRuntimes, numRuntimes=" + numRuntimes );

        Collection<CloudInfo> clouds = pubCloudTable.values();
        for (CloudInfo cloud : clouds) {
            // TODO: get rid of the assumpstion that there is only one pub cloud
            CommChannel contact = cloud.getContact();
            Message msg = msgFactory.createRuntimes( numRuntimes );
            contact.write( msg );
        }
    }


    private void handleCreateRuntimesResp( Message msg ) {
        // TODO: assuming all the runtimesResp msgs are from privClouds
        HashMap<String, String> runtimeTable =
            (HashMap<String, String>)msg.getParam( "runtime_table" );

        String cloudType = (String)msg.getParam( "cloud_type" );

        if (cloudType.equals( "private-cloud" )) {
            privCloudTable.get( msg.getSender() ).updateRuntimeTable( runtimeTable );
            privNumRuntimes = CloudInfo.getTotalNumRuntimes( privCloudTable.values() );

            if (state == STATE_INITIALIZING) {
                state = STATE_READY;
            
                String cosIpAddr = (String)config.get( "ipaddr" );
                int cosPort = ((Integer)config.get( "port" )).intValue();

                System.out.println( "[CosManager] First private runtime created, CosManager READY" );
                System.out.println(
                    "########################### MESSAGE TO USER ###########################" );
                System.out.println(
                    "Please use the following information to start your application" );
                System.out.println( " COS IP address : " + cosIpAddr );
                System.out.println( " COS Port : " + cosPort );
                System.out.println( " Runtimes : " );
                for (String runtimeId : runtimeTable.keySet())
                    System.out.println( "    " + runtimeId );
                System.out.println(
                    "#######################################################################" );
            }

        } else if (cloudType.equals( "public-cloud" )) {
            pubCloudTable.get( msg.getSender() ).updateRuntimeTable( runtimeTable );
            pubNumRuntimes = CloudInfo.getTotalNumRuntimes( pubCloudTable.values() );
        } else {
            System.err.println( "[CosManager] ERROR invalid cloudType=" + cloudType );
        }

        System.out.println( "[CosManager] Runtime created:" + runtimeTable +
                            " on " + msg.getParam( "id" ) );
    }

    
    private void handleCosIfOpen( Message msg ) {
        String appId = (String)msg.getParam( "appid" );
        System.out.println( "[CosManager] handleCosIfOpen, appId=" + appId );
    }


    private void handleCosIfReportNumTasks( Message msg ) {
        String appId = (String)msg.getParam( "appid" );
        int numTasks = (int)msg.getParam( "num_tasks" );

        System.out.println( "[CosManager] handleCosIfReportNumTasks, appId=" + appId +
                            ", numTasks=" + numTasks );

        policy.setTasks( numTasks );
    }
    

    private void handleCosIfRegisterWorkers( Message msg ) {
        String appId = (String)msg.getParam( "appid" );
        ArrayList<String> workerRefs = (ArrayList<String>)msg.getParam( "worker_refs" );
        System.out.println( "[CosManager] handleCosIfRegisterWorkers, appId=" + appId +
                            ", workerRefs=" + workerRefs );

        policy.setNumWorkers( workerRefs.size() );
    }


    private double lastScheduleTimeSec = -1.0;
    private int lastScheduleNumTasks = -1;
    private void handleCosIfReportProgress( Message msg ) {
        String appId = (String)msg.getParam( "appid" );
        int completedTasks = (int)msg.getParam( "completed_tasks" );
        System.out.println( "[CosManager] handleCosIfReportProgress, appId=" + appId +
                            ", completedTasks=" + completedTasks );

        double currentTimeSec = (double)System.currentTimeMillis() / 1000;

        if (lastScheduleTimeSec < 0) {
            policy.start();
            lastScheduleTimeSec = currentTimeSec;
            lastScheduleNumTasks = completedTasks;
            return;
        }
        if ((lastScheduleTimeSec == currentTimeSec) || 
            (lastScheduleNumTasks == completedTasks)) {
            lastScheduleTimeSec = currentTimeSec;
            lastScheduleNumTasks = completedTasks;
            return;
        }

        double tp = (double)(completedTasks - lastScheduleNumTasks) /
            (currentTimeSec - lastScheduleTimeSec);
        tpPredictor.addSample( privNumRuntimes, pubNumRuntimes, tp );

        System.out.println( "[CosManager] privNumRuntimes=" + privNumRuntimes +
                            ", pubNumRuntimes=" + pubNumRuntimes +
                            ", tp=" + tp );
        try {
            policy.schedule( tpPredictor, completedTasks, privNumRuntimes, pubNumRuntimes, 0.0 );
        } catch (Exception ex) {
            System.err.println( ex );
        }
        
        lastScheduleTimeSec = currentTimeSec;
        lastScheduleNumTasks = completedTasks;
    }

    
    private void handleCosIfClose( Message msg ) {
        String appId = (String)msg.getParam( "appid" );
        System.out.println( "[CosManager] handleCosIfClose, appId=" + appId );
    }


    public static void main(String[] args) {
        if (2 != args.length) {
            System.err.println( "[CosManager] invalid arguments" );
            System.exit( 1 );
        }

        CosManager runner = new CosManager( args[0], Integer.parseInt( args[1] ) );
        runner.checkMessages();
    }
}
