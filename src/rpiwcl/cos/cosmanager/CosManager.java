package rpiwcl.cos.cosmanager;

import java.util.*;
import org.ho.yaml.Yaml;
import rpiwcl.cos.cloud.*;
import rpiwcl.cos.common.*;
import rpiwcl.cos.util.*;

// import rpiwcl.cos.cosmanager.policy.PolicyScheculer;


public class CosManager extends Controller {
    private MessageFactory msgFactory;
    // private PolicyScheduler scheduler = null;
    private CommChannel starter;
    private String reconfiguration;
    private HashMap<String, CloudInfo> cloudTable;


    public CosManager( int port ) {
        super( port );
        msgFactory = null;
        starter = null;
        reconfiguration = null;
        cloudTable = new HashMap<String, CloudInfo>();
    }

    public void handleMessage( Message msg ) {
        System.out.println( "[CosManager] Rcved " + msg.getMethod() + " from " + msg.getParam("type") );

        switch( msg.getMethod() ) {
        case "new_connection":
            handleNewConnection( msg );
            break;
            
        case "notify_config":
            handleNotifyConfig( msg );
            break;
        }
    }

    protected void handleNewConnection( Message msg ) {
        if (this.msgFactory == null) {
            this.msgFactory = new MessageFactory( "cos", msg.getReply() );
        }

        if (this.starter == null) {
            // if this is the first connection, it must be from EntityStarter
            this.starter = msg.getReply();
        }
        else {
            children.add( msg.getReply() );
            cloudTable.put( msg.getSender(), new CloudInfo( msg.getSender(), msg.getReply() ) );
        }

        System.out.println( "[CosManager] handleNewConnection, msg.getSender()=" + msg.getSender() );
    }

    protected void handleNotifyConfig( Message msg ) {
        HashMap config = (HashMap)Yaml.load( (String)msg.getParam( "config" ) );
        System.out.println( "[CosManager] config:" + config );
        
        // this.scheduler = new PolicyScheduler( (String)config.get( "policy" ) );
        this.reconfiguration = (String)config.get( "reconfiguration" );
        
        // immediately start CloudControllers
        ArrayList clouds = (ArrayList)config.get( "clouds" );
        for (int i = 0; i < clouds.size(); i++) {
            String cloud = (String)clouds.get( i );
            msg = msgFactory.startEntity( cloud );
System.out.println( "handleNotifyConfig, cloud=" + cloud + ", starter=" + starter );
            starter.write( msg );
        }
    }

    public static void main(String[] args) {
        if (1 != args.length) {
            System.err.println( "[CosManager] invalid arguments" );
            System.exit( 1 );
        }

        CosManager runner = new CosManager( Integer.parseInt( args[0] ) );
        runner.checkMessages();
    }
}
