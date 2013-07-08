package rpiwcl.cos.cloud;

import java.io.IOException;
import java.util.*;
import org.ho.yaml.Yaml;
import rpiwcl.cos.common.*;
import rpiwcl.cos.cloud.CloudController;
import rpiwcl.cos.node.NodeInfo;
import rpiwcl.cos.util.*;

public class PrivCloudController extends Controller {
    private String id;
    private CommChannel starter;
    private CommChannel cos;
    private ArrayList nodes;

    public PrivCloudController( String id, int port, String cosIpAddr, int cosPort ) {
        super( port );
        this.id = id;

        starter = null;
        try {
            cos = new CommChannel( cosIpAddr, cosPort );
        } catch (IOException ioe) {
            System.err.println( "[PrivCloud] ERROR CosManager must be running" );
        }

        ConnectionHandler cosHandler = new ConnectionHandler( cos, mailbox );
        new Thread( cosHandler, "Cos connection" ).start();

        msgFactory = new MessageFactory( id, cos );
    }

    // // sync calls
    // public int getMaxRuntimesNum();
    // public int getCurrentRuntimesNum();
    // public List<RuntimeInfo> getRuntimesInfo();

    // // async calls
    // public void createRuntimes(NodeInfo node, int numRuntimes);
    // public void createRuntimesResp(NodeInfo node, ArrayList<RuntimeInfo> runtimes);


    public void handleMessage( Message msg ) {
        System.out.println( "[PrivCloud] Rcved " + msg.getMethod() + 
                            " from " + msg.getParam( "type" ) );

        switch( msg.getMethod() ) {
        case "new_connection":
            handleNewConnection( msg );
            break;
            
        case "notify_config":
            handleNotifyConfig( msg );
            break;
        }
    }


    private void handleNewConnection( Message msg ) {
        System.out.println( "[PrivCloud] handleNewConnection, msg.getSender()=" + msg.getSender() );

        if (this.starter == null) {
            // if this is the first connection, it must be from EntityStarter
            this.starter = msg.getReply();
        }
        else {
            children.add( msg.getReply() );
            if (children.size() == nodes.size()) {
                System.out.println( "[PrivCloud] Connected to all nodes, PrivCloud READY" );
            }
            // nodeTable.put(msg.getSender(), new NodeInfo(msg.getSender(), msg.getReply()));
        }
    }


    private void handleNotifyConfig( Message msg ) {
        HashMap config = (HashMap)Yaml.load( (String)msg.getParam( "config" ) );
        System.out.println( "[PrivCloud] config:" + config );
        
        // immediately start NodeControllers
        nodes = (ArrayList)config.get( "nodes" );
        for (int i = 0; i < nodes.size(); i++) {
            String node = (String)nodes.get( i );
            msg = msgFactory.startEntity( node );
System.out.println( "handleNotifyConfig, node=" + node + ", starter=" + starter );
            starter.write( msg );
        }
    }


    public static void main( String[] args ) {
        if (4 != args.length) {
            System.err.println( "[PrivCloud] invalid arguments" );
            System.exit( 1 );
        }
        
        // arguments: id, listen_port, parent_ipaddr, parent_port
        PrivCloudController runner = new PrivCloudController( 
            args[0], Integer.parseInt( args[1] ), args[2], Integer.parseInt( args[3] ) );
        runner.checkMessages();
    }

}
