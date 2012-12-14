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
    Messages msgHandler;
    List<String> theaters;

    public NodeController(String cos_addr, int cos_port, int listen_port)
    {
       super(listen_port);
       cos = new CommChannel(cos_addr, cos_port); 
       //Add it to sockeets so we can listen to responses.
       sockets.add(cos);
       lambda = Lambda.getInstance();
       msgHandler = new Messages(cos);
       theaters = null;
    }

    public void periodic()
    {
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
        //Set up so we can track the new vm
        ConStat constat = new ConStat();
        String ip_addr = newbie.getRemoteAddr();
        constat.setIpAddr(ip_addr);
        socketStats.put(newbie, constat);

        //Let COS know a VM has started.
        cos.write(msgHandler.create_vm_response("success", ip_addr));

        //Bring the IOS theater online.
        if( theaters != null )
        {
            String message = msgHandler.create_theater(theaters);
            newbie.write(message);
        }

    }

    public void droppedHook(CommChannel dropped)
    {
        String ip_addr = socketStats.get(dropped).getIpAddr();
        cos.write(msgHandler.destroy_vm_response("success", ip_addr));
        socketStats.remove(dropped);
    }

    public void handleMessage( String message, CommChannel sock)
    {
        String msg;

        Lambda.debugPrint("Node Manager recvd message: " + message);
        switch(msgHandler.get_request_type(message))
        {
            case "notify_low_cpu_usage":
                cos.write(message);
                break;
            case "notify_high_cpu_usage":
                cos.write(message);
                break;
            case "notify_vm_cpu_usage":
                cos.write(message);
                break;
            case "get_cpu_usage":
                for( CommChannel c: socketStats.keySet() )
                {
                    c.write(message);
                }
                
               String nodecpu = msgHandler.notify_node_cpu_usage(lambda.getWeightedSystemLoadAverage());
               cos.write(nodecpu);
               break;
            case "create_vm_request":
                try
                {
                    List<String> params = msgHandler.get_params(message);
                    Runtime.getRuntime().exec("xm create " + params.get(0) ); 
                    theaters = params;
                    theaters.remove(0);
                }
                catch( IOException e)
                {
                    //Should send failure message.
                }
                break;
            case "destroy_vm_request":
                String target = msgHandler.get_params(message).get(0);
                CommChannel victim = findChannelByAddress(target);
                victim.write(msgHandler.shutdown_request());
                break;
            default:
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
