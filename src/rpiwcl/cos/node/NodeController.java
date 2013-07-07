package rpiwcl.cos.node;

import rpiwcl.cos.common.*;
// import rpiwcl.cos.node.NodeInfo;


public class NodeController extends Controller {
    public NodeController( String cloudIpAddr, int cloudPort, int listenPort ) {
        super( listen_port );
    }

    // public int getInstance();
    // public List<Instance> getInstances();
    // // async calls
    // public void startInstances(ArrayList<String> instanceIds);

    public void handleMessage( Message msg ) {
    }

    public static void main( String[] args ) {
        if ( args.length != 1) {
            System.err.println( 
                "Usage: java rpiwcl.cos.node.NodeController <cloud_ipaddr> <cloud_port> <listen_port>" );
            return;
        }

        NodeController runner= NodeController( 
            args[0], Integer.parseInt( args[1] ), Integer.parseInt( args[2] ) );
        runner.checkMessages();
    }
            
}
