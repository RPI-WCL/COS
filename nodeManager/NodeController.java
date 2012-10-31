package nodeManager;

import java.io.*;
import java.util.*;
import java.net.Socket;
import java.lang.Thread;

import common.Controller;
import util.CommChannel;
import util.Lambda;
import util.Messages;

public class NodeController extends Controller
{
    CommChannel cos;
    Lambda lambda;

    public NodeController(String cos_addr, int cos_port, int listen_port)
    {
       super(listen_port);
       cos = new CommChannel(cos_addr, cos_port); 
       lambda = Lambda.getInstance();
    }

    public void periodic()
    {
       String msg = Messages.notify_cpu_usage(lambda.getWeightedSystemLoadAverage());
       cos.write(msg);
    }

    public void handleMessage( String message, CommChannel sock)
    {
        switch(Messages.get_request_type(message))
        {
            case "notify_low_cpu_usage":
                cos.write(message);
                break;
            case "notify_high_cpu_usage":
                cos.write(message);
                break;
            case "create_vm_request":
                try
                {
                    List<String> params = Messages.get_params(message);
                    Runtime.getRuntime().exec("xm create " + params.get(0) ); 
                }
                catch( IOException e)
                {
                    //Should send failure message.
                }
                break;
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
