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
    private HashMap<String, CloudInfo> cloudTable;
    private ArrayList clouds;


    public CosManager( String id, int port ) {
        super( port );
        this.id = id;
        this.state = STATE_INITIALIZING;
        msgFactory = null;
        starter = null;
        reconfiguration = null;
        cloudTable = new HashMap<String, CloudInfo>();
        clouds = null;
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
            cloudTable.put( msg.getSender(), new CloudInfo( msg.getSender(), msg.getReply() ) );
        }
    }

    private void handleNotifyConfig( Message msg ) {
        HashMap config = (HashMap)Yaml.load( (String)msg.getParam( "config" ) );
        Utility.debugPrint( "[CosManager] config:" + config );
        
        // this.scheduler = new PolicyScheduler( (String)config.get( "policy" ) );
        this.reconfiguration = (String)config.get( "reconfiguration" );
        
        // immediately start CloudControllers
        clouds = (ArrayList)config.get( "clouds" );
        for (int i = 0; i < clouds.size(); i++) {
            String cloud = (String)clouds.get( i );
            msg = msgFactory.startEntity( cloud );
            starter.write( msg );
        }
    }

    private int readyReceived = 0;
    private void handleNotifyReady( Message msg ) {
        readyReceived++;

        int runtimeCap = ((Integer)msg.getParam( "runtime_cap" )).intValue();
        cloudTable.get( msg.getSender() ).setRuntimeCapacity( runtimeCap );

        if ((state == STATE_INITIALIZING) && (clouds.size() == readyReceived)) {
            state = STATE_READY;
            System.out.println( "[CosManager] READY received from all clouds, runtimeCap=" + runtimeCap );
        }
        else if (state == STATE_READY) {
            //TODO: new resource is added, notify CosManager the new runtimeCapacity
            System.out.println( "[CosManager] TODO: new resource is added" );
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
