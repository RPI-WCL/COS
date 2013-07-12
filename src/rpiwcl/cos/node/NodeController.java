package rpiwcl.cos.node;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.net.Socket;
import java.lang.Thread;
import org.ho.yaml.Yaml;
import rpiwcl.cos.common.*;
import rpiwcl.cos.util.*;
import rpiwcl.cos.runtime.*;
import rpiwcl.cos.vm.*;


public class NodeController extends Controller {
    private String id;
    private CommChannel starter;
    private CommChannel cloud;

    private HashMap config;
    private HashMap cpuDb;
    private boolean useVm;
    private String vmUser;
    private String vmImage;

    private HashMap<String, VmInfo> vmTable;
    private String cpu;
    private int numRuntimesLimit;
    private HashSet<String> runtimeIds;

    private AppRuntime appRuntime;
    private HashMap appRuntimeConf;


    public NodeController( String id, int port, String cloudIpAddr, int cloudPort ) {
        super( port );
        this.id = id;
        this.state = STATE_INITIALIZING;
        starter = null;
        cloud = null;

        // extracted from config
        config = null;
        cpuDb = null;
        useVm = false;
        vmUser = null;
        vmImage = null;

        vmTable = new HashMap<String, VmInfo>();
        numRuntimesLimit = 0;
        runtimeIds = new HashSet<String>();

        // connecting to CloudController
        try {
            cloud = new CommChannel( cloudIpAddr, cloudPort );
        } catch (IOException ioe) {
            System.err.println( "[Node] ERROR parent CloudController must be running" );
        }
        ConnectionHandler cloudHandler = new ConnectionHandler( cloud, mailbox );
        new Thread( cloudHandler, "Cloud connection" ).start();

        msgFactory = new MessageFactory( id, cloud );

        // cpuinfo
        String str = Utility.runtimeExecWithStdout(
            "cat /proc/cpuinfo | grep 'model name' | cut -c14- | uniq" );
        Pattern pt = Pattern.compile( "\n\\Z" );
        Matcher match = pt.matcher( str );
        cpu = match.replaceAll( "" );
        System.out.println( "[Node] " + cpu + " found" );

        appRuntime = null;
    }


    private CommChannel findChannelByAddress( String addr ) {
        for (CommChannel s : children) {
            if (s.getRemoteAddr().equals(addr) ) {
                return s;
            }
        }
        return null;
    }


    public void handleMessage( Message msg ){
        System.out.println( "[Node] Rcved: " + msg.getMethod() +
                            " from " + msg.getParam( "id" ) );

        switch(msg.getMethod()) {
            // case "get_usage":
            //     handleGetUsage(msg);
            //     break;
        case "new_connection":          // from EntityStarter/VmController
            handleNewConnection( msg );
            break;
        case "notify_config":           // from EntityStarter
            handleNotifyConfig( msg );
            notifyReady();
            break;
        case "create_runtimes":         // from CloudController
            handleCreateRuntimes( msg );
            break;
        case "start_runtime_resp":      // from EntityStarter
            handleStartRuntimeResp( msg );
            break;
            // case "create_vm":
            //     create_vm(msg);
            //     break;
            // case "destroy_vm":
            //     String addr = (String) msg.getParam("target_vm");
            //     CommChannel target = findChannelByAddress(addr);
            //     target.write(msg);
            //     break;
        case "dropped_connection":
            droppedConnection(msg);
        break;
        default:
            //If we don't know how to deal with it, pass it up.
            //cloud.write(msg);
            break;
        }
    }


    // private void handleGetUsage(Message msg) {
    //     broadcast(msg);

    //     Usage usage = new Usage();
    //     Message resp = msgFactory.usageResponse(usage);
    //     cloud.write(resp);
    // }

    private void handleNewConnection( Message msg ) {
        if (this.starter == null) {
            // if this is the first connection, it must be from EntityStarter
            this.starter = msg.getReply();
        }
        else {
            children.add( msg.getReply() );
            //We have a new connection. We should let COS know.
            Message payload = msgFactory.vmCreation( msg.getSender() );
            cloud.write(payload);
        }
        // if(theaters != null)
        //     msg.getReply().write(msgFactory.startTheater(theaters));
    }


    private void handleNotifyConfig( Message msg ) {
        config = (HashMap)Yaml.load( (String)msg.getParam( "config" ) );
        Utility.debugPrint( "[Node] config:" + config );

        useVm = ((Boolean)config.get( "use_vm" )).booleanValue() &&
            ((Boolean)config.get( "vm_support" )).booleanValue();
        vmUser = (String)config.get( "vm_user" );
        vmImage = (String)config.get( "vm_image" );
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
    }


    int respRemain = 0;
    HashSet<String> newRuntimeIds = null;
    public void handleCreateRuntimes( Message msg ) {
        // TODO: PrivCloudController does not send a next createRuntimes request until it receives the response --> remove this limitation
        
        int numRuntimes = ((Integer)msg.getParam( "num_runtimes" )).intValue();
        System.out.println( "[Node] handleCreateRuntimes, numRuntimes=" + numRuntimes );

        if (!useVm) {
            // create runtimes on PM
            respRemain = numRuntimes;
            newRuntimeIds = new HashSet<String>();

            RuntimeInfo runtime = appRuntime.createRuntime( appRuntimeConf );
            Message request = msgFactory.startRuntime( runtime );
            starter.write( request );
        }
        else {
            // TODO: create runtimes on PM
            System.err.println( "[Node] ERROR, runtime creation on VM not supported" );
        }
    }

    public void handleStartRuntimeResp( Message msg ) {
        String runtimeId = (String)msg.getParam( "runtime_id" );
        String result =  (String)msg.getParam( "result" );
        System.out.println( "[Node] handleStartRuntimeResp, runtimeId=" + runtimeId + 
                            ", result=" + result );
        respRemain--;
        runtimeIds.add( runtimeId );
        newRuntimeIds.add( runtimeId );

        if (0 < respRemain ) {
            RuntimeInfo runtime = appRuntime.createRuntime( appRuntimeConf );
            Message request = msgFactory.startRuntime( runtime );
            starter.write( request );
        }
        else {
            Message resp = msgFactory.createRuntimesResp( newRuntimeIds );
            cloud.write( resp );
        }
    }


    private void droppedConnection( Message msg ){
        String addr = (String) msg.getParam( "dropped_connection" );
        Message result = msgFactory.vmDestruction( addr );
        cloud.write( result );
        children.remove( msg.getReply() );
    }


    private void notifyReady() {
        Integer cpumark = (Integer)cpuDb.get( cpu );
        HashMap common = (HashMap)config.get( "common" );
        Integer cpumarkPerRuntime = (Integer)common.get( "cpumark_per_runtime" );
        numRuntimesLimit = cpumark / cpumarkPerRuntime;

        System.out.println( "[Node] cpuMark=" + cpumark +
                            ", cpumarkPerRuntime=" + cpumarkPerRuntime +
                            ", numRuntimesLimit=" + numRuntimesLimit );
        
        Message msg = msgFactory.notifyReady( new Integer( numRuntimesLimit ), "node" );
        cloud.write( msg );

        state = STATE_READY;
        System.err.println( "[Node] NodeController READY" );
    }


    // private void create_vm(Message msg){
    //     //TODO: Should fix this warning eventually,
    //     theaters = (LinkedList<String>) msg.getParam("theaters");
    //     try{
    //         Runtime.getRuntime().exec("xm create " + Constants.PATH_TO_VM_IMAGE);
    //         //Runtime.getRuntime().exec("Terminal -x java vmManager.VMController 127.0.0.1");
    //     } catch(IOException e){
    //         e.printStackTrace();
    //         //TODO: Notify of failure
    //     }
    // }


    public static void main( String [] args ) throws Exception{
        if (4 != args.length) {
            System.err.println( "[Node] invalid arguments" );
            System.exit( 1 );
        }

        // arguments: id, listen_port, parent_ipaddr, parent_port
        NodeController runner = new NodeController( 
            args[0], Integer.parseInt( args[1] ), args[2], Integer.parseInt( args[3] ) );
        runner.checkMessages();
    }
}
