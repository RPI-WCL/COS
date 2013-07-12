package rpiwcl.cos.cloud;

import java.io.IOException;
import java.util.*;
import org.ho.yaml.Yaml;
import rpiwcl.cos.common.*;
import rpiwcl.cos.cloud.CloudController;
import rpiwcl.cos.node.NodeInfo;
import rpiwcl.cos.util.*;
import rpiwcl.cos.vm.*;

public class AmazonEC2Controller extends Controller {
    private CommChannel starter;
    private CommChannel cos;
    private HashMap config;
    private HashMap cpuDb;
    private HashMap<String, VmInfo> vmTable;
    private int runtimeCapacity;


    public AmazonEC2Controller( String id, int port, String cosIpAddr, int cosPort ) {
        super( port );
        this.id = id;
        this.state = STATE_INITIALIZING;
        starter = null;
        config = null;
        cpuDb = null;
        vmTable = new HashMap<String, VmInfo>();


        try {
            cos = new CommChannel( cosIpAddr, cosPort );
        } catch (IOException ioe) {
            System.err.println( "[AmazonEC2] this should not happen, CosManager must be running" );
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
        System.out.println( "[AmazonEC2] Rcved " + msg.getMethod() + 
                            " from " + msg.getParam( "type" ) );

        switch( msg.getMethod() ) {
        case "new_connection":
            handleNewConnection( msg );
            break;
        case "notify_config":
            handleNotifyConfig( msg );
            notifyReady();
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
            vmTable.put(msg.getSender(), new VmInfo(msg.getSender(), msg.getReply()));
        }
    }


    protected void handleNotifyConfig( Message msg ) {
        config = (HashMap)Yaml.load( (String)msg.getParam( "config" ) );
        Utility.debugPrint( "[AmazonEC2] config:" + config );
        cpuDb = (HashMap)config.get( "cpu_db" );
    }


    private void notifyReady() {
        HashMap common = (HashMap)config.get( "common" );
        Message msg = msgFactory.notifyReady( 0 );
        cos.write( msg );
        System.err.println( "[AmazonEC2] AmazonEC2Controller READY" );
        state = STATE_READY;
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
