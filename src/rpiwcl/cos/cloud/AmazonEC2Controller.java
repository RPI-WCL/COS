package rpiwcl.cos.cloud;

import java.io.IOException;
import java.util.*;
import org.ho.yaml.Yaml;
import rpiwcl.cos.common.*;
import rpiwcl.cos.cloud.CloudController;
import rpiwcl.cos.node.NodeInfo;
import rpiwcl.cos.util.*;

public class AmazonEC2Controller extends Controller {
    private String id;
    private CommChannel starter;
    private CommChannel cos;
    private ArrayList cpuDb;
    private String cosIpAddr;
    private int cosPort;

    public AmazonEC2Controller( String id, int port, String cosIpAddr, int cosPort ) {
        super( port );
        this.id = id;
        this.cosIpAddr = cosIpAddr;
        this.cosPort = cosPort;
        
        starter = null;
        cos = null;
        cpuDb = null;

        msgFactory = new MessageFactory( id );
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
            requestCpuDb();
            break;
        case "request_cpu_db_resp":
            handleRequestCpuDbResp( msg );
            connectCos();
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
            // children.add(msg.getReply());
            // nodeTable.put(msg.getSender(), new NodeInfo(msg.getSender(), msg.getReply()));
        }
    }


    protected void handleNotifyConfig( Message msg ) {
        HashMap config = (HashMap)Yaml.load( (String)msg.getParam( "config" ) );
        System.out.println( "[AmazonEC2] config:" + config );
    }


    private void requestCpuDb() {
        System.out.println( "[AmazonEC2] requesting CpuDB" );
        Message msg = msgFactory.requestCpuDb();
        starter.write( msg );
    }


    private void connectCos() {
        try {
            cos = new CommChannel( cosIpAddr, cosPort );
        } catch (IOException ioe) {
            System.err.println( "[AmazonEC2] this should not happen, CosManager must be running" );
        }

        ConnectionHandler cosHandler = new ConnectionHandler( cos, mailbox );
        new Thread( cosHandler, "Cos connection" ).start();
    }


    private void handleRequestCpuDbResp( Message msg ) {
        cpuDb = (ArrayList)Yaml.load( (String)msg.getParam( "cpu_db" ) );
        System.out.println( "[AmazonEC2] cpuDb: " + cpuDb );
        System.out.println( "[AmazonEC2] AmazonEC2 READY" );
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
