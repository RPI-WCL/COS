package rpiwcl.cos.manager;
import java.util.*;
import org.ho.yaml.Yaml;
import rpiwcl.cos.cloud.*;
import rpiwcl.cos.common.*;
import rpiwcl.cos.util.*;

// import rpiwcl.cos.cosmanager.policy.PolicyScheculer;


public class CosManager extends Controller {
    private String id;
    private MessageFactory msgFactory;
    // private PolicyScheduler scheduler = null;
    private CommChannel starter;
    private String reconfiguration;

    private HashMap<String, CloudInfo> privCloudTable;
    private ArrayList privClouds;
    private HashMap<String, CloudInfo> pubCloudTable;
    private ArrayList pubClouds;


    public CosManager( String id, int port ) {
        super( port );
        this.id = id;
        this.state = STATE_INITIALIZING;
        msgFactory = null;
        starter = null;
        reconfiguration = null;
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
        HashMap config = (HashMap)Yaml.load( (String)msg.getParam( "config" ) );
        Utility.debugPrint( "[CosManager] config:" + config );
        
        // this.scheduler = new PolicyScheduler( (String)config.get( "policy" ) );
        this.reconfiguration = (String)config.get( "reconfiguration" );
        
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
        int numRuntimesLimit = ((Integer)msg.getParam( "num_runtimes_limit" )).intValue();
        String type = (String)msg.getParam( "type" );

        CloudInfo cloud = new CloudInfo( msg.getSender(), msg.getReply() );
        cloud.setNumRuntimesLimit( numRuntimesLimit );
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
            System.out.println( "[CosManager] READY received from all clouds, numRuntimesLimit=" + numRuntimesLimit );
            
            createPrivRuntimes( Constants.INITIAL_RUNTIME_NUM );
        }
        else if (state == STATE_READY) {
            //TODO: new resource is added, notify CosManager the new numRuntimesLimit
            System.out.println( "[CosManager] TODO: new resource is added" );
        }
    }


    private boolean createPrivRuntimes( int numRuntimes ) {
        System.out.println( "[CosManager] createPrivRuntimes, numRuntimes=" + numRuntimes );

        int totalNumRuntimesLimit = CloudInfo.getTotalNumRuntimesLimit( privCloudTable.values() );
        int totalNumRuntimesInUse = CloudInfo.getTotalNumRuntimesInUse( privCloudTable.values() );

        if ((totalNumRuntimesLimit - totalNumRuntimesInUse) < numRuntimes) {
            System.err.println( "[CosManager] no enough room to create " + 
                                numRuntimes + " runtimes (limit=" + 
                                totalNumRuntimesLimit + ", inUse=" +
                                totalNumRuntimesInUse + ")" );
            return false;
        }
        else {
            Collection<CloudInfo> clouds = privCloudTable.values();
            int numRemain = numRuntimes;
            for (CloudInfo cloud : clouds) {
                int numNotInUse = cloud.getNumRuntimesLimit() - cloud.getNumRuntimes();
                int numRequest =  (numNotInUse < numRemain) ? numRemain - numNotInUse : numRemain;

                CommChannel contact = cloud.getContact();
                Message msg = msgFactory.createRuntimes( new Integer( numRequest ) );
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


    private void handleCreateRuntimesResp( Message msg ) {
        HashMap<String, String> runtimeTable =
            (HashMap<String, String>)msg.getParam( "runtime_table" );

        privCloudTable.get( msg.getSender() ).updateRuntimeTable( runtimeTable );

        if (state == STATE_INITIALIZING) {
            state = STATE_READY;
            System.out.println( "[CosManager] First runtime created, CosManager READY" );
            System.out.println();
            System.out.println(
                "########################### MESSAGE TO USER ###########################" );
            System.out.println(
                "Please use the following runtime information to start your application" );
            for (String runtimeId : runtimeTable.keySet())
                System.out.println( "    " + runtimeId );
            System.out.println(
                "#######################################################################" );
        }
        else {
            System.out.println( "[CosManager] Runtime created:" + runtimeTable );
        }
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
