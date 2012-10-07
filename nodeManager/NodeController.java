package nodeManager;

import java.io.PrintWriter;
import java.net.Socket;
import java.lang.Thread;

import common.Controller;
import util.CommChannel;
import util.Messages;

public class NodeController extends Controller
{
    CommChannel cos;

    public NodeController(String cos_addr, int cos_port, int listen_port)
    {
       super(listen_port);
       cos = new CommChannel(cos_addr, cos_port); 
    }

    public void handleMessage( String message, CommChannel sock)
    {
        switch(Messages.get_request_type(message))
        {
            default:
                System.out.println("NodeController recvd message: " + message);
                cos.write(message);
                break;
        }
    }

    public static void main( String [] args ) throws Exception
    {
        if( args.length != 3)
            return;
        NodeController runner = new NodeController( args[0], 
                                    Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        runner.checkMessages();
    }
}
