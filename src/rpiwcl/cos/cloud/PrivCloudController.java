package rpiwcl.cos.cloud;

import java.io.IOException;
import java.util.*;
import org.ho.yaml.Yaml;
import rpiwcl.cos.common.*;
import rpiwcl.cos.node.NodeInfo;
import rpiwcl.cos.util.*;

public class PrivCloudController extends Controller {
    private CommChannel starter;
    private CommChannel cos;
    private ArrayList nodes;
    private HashMap config;
    private HashMap<String, NodeInfo> nodeTable;
    private int maxRuntimes;


    public PrivCloudController( String id, int port, String cosIpAddr, int cosPort ) {
        super( port );
        this.id = id;
        this.state = STATE_INITIALIZING;
        starter = null;
        config = null;
        nodeTable = new HashMap<String, NodeInfo>();
        maxRuntimes = 0;

        // connecting to CosManager
        try {
            cos = new CommChannel( cosIpAddr, cosPort );
        } catch (IOException ioe) {
            System.err.println( "[PrivCloud] this should not happen, CosManager must be running" );
        }
        ConnectionHandler cosHandler = new ConnectionHandler( cos, mailbox );
        new Thread( cosHandler, "Cos connection" ).start();

        msgFactory = new MessageFactory( id, cos );
    }


    public void handleMessage( Message msg ) {
        System.out.println( "[PrivCloud] Rcved " + msg.getMethod() + 
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
        case "create_runtimes":
            handleCreateRuntimes( msg );
            break;
        case "create_runtimes_resp":    // from NodeController
            handleCreateRuntimesResp( msg );
            break;
        }
    }



    private void handleNewConnection( Message msg ) {
        if (this.starter == null) {
            // if this is the first connection, it must be from EntityStarter
            this.starter = msg.getReply();
        }
        else {
            // otherwise, it is from NodeController
            children.add( msg.getReply() );
            nodeTable.put(msg.getSender(), new NodeInfo(msg.getSender(), msg.getReply()));
        }
    }


    private void handleNotifyConfig( Message msg ) {
        config = (HashMap)Yaml.load( (String)msg.getParam( "config" ) );
        // System.out.println( "[PrivCloud] config:" + config );
        
        // start NodeControllers
        nodes = (ArrayList)config.get( "nodes" );
        for (int i = 0; i < nodes.size(); i++) {
            String node = (String)nodes.get( i );
            msg = msgFactory.startEntity( node );
            starter.write( msg );
        }
    }


    private int readyReceived = 0;
    private void handleNotifyReady( Message msg ) {
        readyReceived++;

        int receivedNum = (int)msg.getParam( "max_runtimes" );
        nodeTable.get( msg.getSender() ).setMaxRuntimes( receivedNum );
        maxRuntimes += receivedNum;

        if ((state == STATE_INITIALIZING) && (nodes.size() == readyReceived)) {
            Message ready = msgFactory.notifyReady( maxRuntimes, "private-cloud" );
            cos.write( ready );
            state = STATE_READY;
            System.out.println( "[PrivCloud] READY received from all nodes, notify CosManager" );
        }
        else if (state == STATE_READY) {
            //TODO: new resource is added, notify CosManager the new maxRuntimes
            System.out.println( "[PrivCloud] TODO: new resource is added" );
        }
    }

    int respRemain = 0;
    HashMap<String, String> newRuntimeTable = null;
    public void handleCreateRuntimes( Message msg ) {
        int numRuntimes = ((Integer)msg.getParam( "num_runtimes" )).intValue();
        int totalMaxRuntimes = NodeInfo.getTotalMaxRuntimes( nodeTable.values() );
        int totalNumRuntimes = NodeInfo.getTotalNumRuntimes( nodeTable.values() );

        if ((totalMaxRuntimes - totalNumRuntimes) < numRuntimes) {
            System.err.println( "[PrivCloud] no enough room to create " + 
                                numRuntimes + " runtimes (max=" + 
                                totalMaxRuntimes + ", =" +
                                totalNumRuntimes + ")" );
        }
        else {
            System.out.println( "[PrivCloud] handleCreateRuntimes, requested " + numRuntimes
                                + " runtimes (max=" + totalMaxRuntimes +
                                ", =" + totalNumRuntimes + ")" );

            Collection<NodeInfo> nodes = nodeTable.values();

            int numRemain = numRuntimes;
            for (NodeInfo node : nodes) {
                int numNot = node.getMaxRuntimes() - node.getNumRuntimes();
                int numRequest =  (numNot < numRemain) ? numRemain - numNot : numRemain;

                CommChannel contact = node.getContact();
                Message req = msgFactory.createRuntimes( numRequest );
                contact.write( req );

                numRemain -= numRequest;
                if (numRemain == 0)
                    break;
            }

            // keep track of responses
            respRemain = numRuntimes;
            newRuntimeTable = new HashMap<String, String>();
        }
    }

    public void handleCreateRuntimesResp( Message msg ) {
        // update NodeInfo
        HashSet<String> runtimeIds = (HashSet<String>)msg.getParam( "runtime_ids" );
        NodeInfo node = nodeTable.get( msg.getSender() );
        node.addRuntimeIds( runtimeIds );
        nodeTable.put( msg.getSender(), node );

        respRemain -= runtimeIds.size();
        for (String runtimeId : runtimeIds )
            newRuntimeTable.put( runtimeId, msg.getSender() );

        System.out.println( "[PrivCloud] handleCreateRuntimesResp, respRemain=" + respRemain );

        if (respRemain == 0) {
            // pass the same response to CosManager
            System.out.println( "[PrivCloud] handleCreateRuntimesResp, sending response to CosManager: " + newRuntimeTable );
            Message resp = msgFactory.createRuntimesResp( "private-cloud", newRuntimeTable );
            cos.write( resp );
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
