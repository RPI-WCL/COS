package cosManager;

import java.io.*;
import java.lang.System;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import common.Controller;
import cosManager.ConStat;
import util.CommChannel;
import util.Messages;
import util.Lambda;

public class COSController extends Controller
{

    HashMap<String, ConStat> vmList;
    final int maxVm = 10;
    final int minVm = 1;
    final long timestep = 2 * 60;
    final String vmCfgFile = "/etc/xen/cos/jcos01.cfg";
    final String theaterFile = "./theaters.txt";
    long timeDelay;

    public COSController()
    {
        super(9999);
        vmList = new HashMap<String, ConStat>();
        timeDelay = 0;
    }

    public void newHook(CommChannel newbie)
    {
       ConStat constat = new ConStat();
       socketStats.put(newbie, constat);
    }

    public void droppedHook(CommChannel dropped)
    {
        socketStats.remove(dropped);
        //Ignore for now. Should mark it as disabled with
        //the option of waking it with a magic packet.
    }

    public void periodic()
    {
        double avg = ConStat.avg(vmList);
        long next = System.currentTimeMillis();

        if( avg < .3 && next - timeDelay < timestep && vmList.size() > minVm )
        {
            String key = ConStat.findMinVm(vmList);
            ConStat alpha = vmList.get(key);
            String msg = Messages.destroy_vm_request(key);
            alpha.getParent().write(msg);
            timeDelay = System.currentTimeMillis();
        }
        else if( avg > .9 && next - timeDelay < timestep && vmList.size() < maxVm )
        {
            CommChannel node = ConStat.findMinNode(socketStats);
            //And empty iterable defaults to using "./theaters.txt"
            String msg = Messages.create_vm_request(vmCfgFile, new LinkedList<String>());
            node.write(msg);
            timeDelay = System.currentTimeMillis();
        }
    }

    public void handleMessage( String message, CommChannel sock)
    {
        String cpu_usage;
        String return_addr;
        List<String> params;

        Lambda.debugPrint("COS Controller recvd message: " + message);

        switch(Messages.get_request_type(message))
        {
            case "create_vm_response":
                params = Messages.get_params(message);
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
                params = Messages.get_params(message);
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
                cpu_usage = Messages.get_params(message).get(0);
                return_addr = Messages.get_return_addr(message);
                vmList.get(return_addr).update(Double.parseDouble(cpu_usage));
                break;
            case "notify_low_cpu_usage":
                cpu_usage = Messages.get_params(message).get(0);
                return_addr = Messages.get_return_addr(message);
                vmList.get(return_addr).update(Double.parseDouble(cpu_usage));
                break;
            case "notify_cpu_usage":
                cpu_usage = Messages.get_params(message).get(0);
                socketStats.get(sock).update(Double.parseDouble(cpu_usage));
                break;
        }
    }

    public static void main(String[] args) throws Exception
    {
        COSController runner = new COSController();
        runner.checkMessages();
    }
}

