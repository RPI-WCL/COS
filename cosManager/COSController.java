package cosManager;

import java.io.*;
import java.lang.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import common.Controller;
import common.Constants;
import cosManager.ConStat;
import util.CommChannel;
import util.Messages;
import util.Lambda;

public class COSController extends Controller
{

    HashMap<String, ConStat> vmList;
    final int maxVm = 2;
    final int minVm = 1;
    final long timestep = 2 * 60;
    final String vmCfgFile = "/etc/xen/cos/jcos01.cfg";
    final String theaterFile = "./theaters.txt";
    long timeDelay;
    Messages msgHandler;
    boolean isWaitingResponses;
    int remaining;
    int errorcount;
    double error;
    double prediction;
    double timebackoff;
    double prev_mag;
    double prev_sum;
    boolean created;
    boolean destroyed;
    boolean lock;

    public COSController()
    {
        super(Constants.COS_PORT);
        vmList = new HashMap<String, ConStat>();
        timeDelay = 0;
        error = 0;
        timebackoff = 1;
        errorcount = 0;
        msgHandler = null;
        isWaitingResponses = false;
        created = false;
        destroyed = false;
        lock = false;
    }

    public void newHook(CommChannel newbie)
    {
        ConStat constat = new ConStat();
        socketStats.put(newbie, constat);
        if( msgHandler == null )
            msgHandler = new Messages(newbie);
       
    }

    public void droppedHook(CommChannel dropped)
    {
        socketStats.remove(dropped);
        //Ignore for now. Should mark it as disabled with
        //the option of waking it with a magic packet.
    }

    public void periodic()
    {
        if( remaining != 0 || lock )
        {
            return;
        }
        isWaitingResponses = false;
        if( !ConStat.canChange(vmList) )
        {
            System.out.println("Can't do shit.");
            return;
        }


        long next = System.currentTimeMillis();
        double magnitude = ConStat.magnitude(vmList);
        double sum = ConStat.signedSum(vmList);
        if(created || destroyed)
        {
            if(destroyed && prev_mag < magnitude )
            {
                    errorcount++;
            }
            if( created )
            {
                if( prev_mag < magnitude && prev_sum  > sum )
                {
                    errorcount++;
                }
            }
            created = false;
            destroyed = false;
        }
        error = Math.abs(prev_mag - magnitude);
        

        if( magnitude - ConStat.predictDestroy(vmList) > error * errorcount / timebackoff  && vmList.size() -1 > minVm)
        {
            prev_mag = magnitude;
            System.out.println("Destroy!");
            String key = ConStat.findMinVm(vmList);
            ConStat alpha = vmList.get(key);
            String msg = msgHandler.destroy_vm_request(key);
            alpha.getParent().write(msg);
            timeDelay = System.currentTimeMillis();
            lock = true;
        }
        else if( magnitude - ConStat.predictCreate(vmList) > error * errorcount / timebackoff &&  vmList.size() < maxVm )
        {
            prev_mag = magnitude;
            System.out.println("CREATE!");
            CommChannel node = ConStat.findMinNode(socketStats);
            String msg = msgHandler.create_vm_request(vmCfgFile, new LinkedList<String>());
            node.write(msg);
            timeDelay = System.currentTimeMillis();
            lock = true;
        }
    }

    public void handleMessage( String message, CommChannel sock)
    {
        String cpu_usage;
        String return_addr;
        List<String> params;

        Lambda.debugPrint("COS Controller recvd message: " + message);

        switch(msgHandler.get_request_type(message))
        {
            case "create_vm_response":
                params = msgHandler.get_params(message);
                lock = false;
                if(params.get(0).equals("success"))
                {
                    String ip_addr = params.get(1);
                    ConStat newbie = new ConStat();
                    newbie.setParent(sock);
                    newbie.setIpAddr(ip_addr);

                    vmList.put(ip_addr, newbie); 
                }
                else
                {
                    //VM creation failed. It should attempt to be created again.
                }
                break;
            case "destroy_vm_response":
                params = msgHandler.get_params(message);
                lock = false;
                if(params.get(0).equals("success"))
                {
                    String ip_addr = params.get(1);
                    vmList.remove(ip_addr);
                }
                else
                {
                    //VM deletion failed. It should be attempted again
                }
                break;
            case "notify_high_cpu_usage":
                cpu_usage = msgHandler.get_params(message).get(0);
                return_addr = msgHandler.get_return_addr(message);
                vmList.get(return_addr).update(Double.parseDouble(cpu_usage));
                if( !isWaitingResponses )
                {
                    String payload = msgHandler.get_cpu_usage();
                    broadcast(payload);
                    isWaitingResponses = true;
                    remaining = vmList.size() + socketStats.size();
                }
                break;
            case "notify_low_cpu_usage":
                cpu_usage = msgHandler.get_params(message).get(0);
                return_addr = msgHandler.get_return_addr(message);
                vmList.get(return_addr).update(Double.parseDouble(cpu_usage));
                if( !isWaitingResponses )
                {
                    String payload = msgHandler.get_cpu_usage();
                    broadcast(payload);
                    isWaitingResponses = true;
                    remaining = vmList.size() + socketStats.size();
                }
                break;
            case "notify_vm_cpu_usage":
                cpu_usage = msgHandler.get_params(message).get(0);
                return_addr = msgHandler.get_return_addr(message);
                vmList.get(return_addr).update(Double.parseDouble(cpu_usage));
                remaining--;
                break;
            case "notify_node_cpu_usage":
                cpu_usage = msgHandler.get_params(message).get(0);
                socketStats.get(sock).update(Double.parseDouble(cpu_usage));
                remaining--;
                break;
        }
    }

    public void broadcast(String payload)
    {
        for( CommChannel c: socketStats.keySet() )
        {
            c.write(payload);
        }
    }

    public static void main(String[] args) throws Exception
    {
        COSController runner = new COSController();
        runner.checkMessages();
    }
}

