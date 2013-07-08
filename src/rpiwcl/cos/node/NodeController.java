package rpiwcl.cos.node;

import java.io.*;
import java.util.*;
import java.net.Socket;
import java.lang.Thread;
import org.ho.yaml.Yaml;
import rpiwcl.cos.common.*;
import rpiwcl.cos.util.*;


public class NodeController extends Controller {
    private String id;
    private CommChannel starter;
    private CommChannel cloud;
    private boolean useVm;
    private boolean vmSupport;
    private String vmUser;
    private String vmImage;
    
    
    // LinkedList<String> theaters;

    public NodeController( String id, int port, String cloudIpAddr, int cloudPort ) {
        super( port );
        this.id = id;

        starter = null;
        try {
            cloud = new CommChannel( cloudIpAddr, cloudPort );
        } catch (IOException ioe) {
            System.err.println( "[Node] ERROR parent CloudController must be running" );
        }

        ConnectionHandler cloudHandler = new ConnectionHandler( cloud, mailbox );
        new Thread( cloudHandler, "Cloud connection" ).start();

        useVm = false;
        vmSupport = false;
        vmUser = null;
        vmImage = null;

        msgFactory = new MessageFactory( id, cloud );
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
                            " from " + msg.getParam( "type" ) );

        switch(msg.getMethod()) {
            // case "get_usage":
            //     handleGetUsage(msg);
            //     break;
        case "new_connection":
            handleNewConnection( msg );
            break;
        case "notify_config":
            handleNotifyConfig( msg );
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
        HashMap config = (HashMap)Yaml.load( (String)msg.getParam( "config" ) );
        System.out.println( "[Node] config:" + config );
        useVm = ((Boolean)config.get( "use_vm" )).booleanValue();
        vmSupport = ((Boolean)config.get( "vm_support" )).booleanValue();
        vmUser = (String)config.get( "vm_user" );
        vmImage = (String)config.get( "vm_image" );
    }


    private void droppedConnection( Message msg ){
        String addr = (String) msg.getParam( "dropped_connection" );
        Message result = msgFactory.vmDestruction( addr );
        cloud.write( result );
        children.remove( msg.getReply() );
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
