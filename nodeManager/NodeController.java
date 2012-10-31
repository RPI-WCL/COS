package nodeManager;

import java.io.*;
import java.util.*;
import java.net.Socket;
import java.lang.Thread;

import common.Controller;
import cosManager.ConStat;
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
       //Add it to sockeets so we can listen to responses.
       sockets.add(cos);
       lambda = Lambda.getInstance();
    }

    public void periodic()
    {
       String msg = Messages.notify_cpu_usage(lambda.getWeightedSystemLoadAverage());
       cos.write(msg);
    }

    private CommChannel findChannelByAddress(String addr)
    {
        for( CommChannel s : sockets)
        {
            if( s.getRemoteAddr().equals(addr) )
            {
                return s;
            }
        }
        return null;
    }

    public void newHook(CommChannel newbie)
    {
        ConStat constat = new ConStat();
        String ip_addr = newbie.getRemoteAddr();
        constat.setIpAddr(ip_addr);
        socketStats.put(newbie, constat);
        cos.write(Messages.create_vm_response("success", ip_addr));
    }

    public void droppedHook(CommChannel dropped)
    {
        String ip_addr = socketStats.get(dropped).getIpAddr();
        cos.write(Messages.destroy_vm_response("success", ip_addr));
        socketStats.remove(dropped);
    }

    public void handleMessage( String message, CommChannel sock)
    {
        String msg;
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
            case "destroy_vm_request":
                String target = Messages.get_params(message).get(0);
                CommChannel victim = findChannelByAddress(target);
                victim.write(Messages.shutdown_request());
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
