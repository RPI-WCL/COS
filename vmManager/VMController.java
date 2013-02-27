package vmManager;

import java.io.*;
import java.util.*;
import java.net.Socket;
import java.lang.Thread;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import common.Constants;
import common.ConnectionHandler;
import common.Message;
import common.MessageFactory;
import util.CommChannel;
import util.Utility;

public class VMController
{
    private CommChannel hostNode;
    private LinkedList<Double> cpuHistory;
    
    LinkedBlockingQueue<Message> mailbox;
    MessageFactory msgFactory;

    private VMController(String addr, int port){
        hostNode = new CommChannel(addr, port);
        mailbox = new LinkedBlockingQueue<Message>();
        cpuHistory = new LinkedList<Double>();
        ConnectionHandler listenLoop = new ConnectionHandler(hostNode, mailbox);
        new Thread(listenLoop, "Socket Listener").start();
        
        msgFactory = new MessageFactory("VM", hostNode);
    }

    private void createTheater(Iterable<String> theaters){
        try{
            FileWriter fstream = new FileWriter("theaters.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            for( String s: theaters)
                out.write( theaters + "\n" );
            out.close();

            Runtime.getRuntime().exec(Constants.LAUNCH_IOS);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void handleMessage(Message msg){
        Message resp;
        Utility.debugPrint("VM recvd message: " + msg.getMethod());
        switch(msg.getMethod())
        {
            case "shutdown_theater_request":
                //Runtime.getRuntime().exec("ls");
                break;
            case "start_theater":
                LinkedList<String> peers = (LinkedList<String>) msg.getParam("peers");
                createTheater(peers);
                break;
            case "get_cpu_usage":
                double load = Utility.getWeightedSystemLoadAverage();
                resp = msgFactory.cpuUsageResp(load); 
                hostNode.write(resp);
                break;
            case "destroy_vm":
                if( true ){
                    System.exit(1);
                }
                try{
                    Runtime.getRuntime().exec("shutdown -h now");
                } catch(IOException e){
                    //Report out to Node Controller. Tell it to destroy me.
                }
                break;
        }
    }

    private void run()
    {
        double load;
        Message msg;
        while(true)
        {
            try{
                msg = mailbox.poll(10L, TimeUnit.SECONDS);
                if( msg != null )
                    handleMessage(msg);
            } catch(InterruptedException e){
                e.printStackTrace();
            }

            load = Utility.getWeightedSystemLoadAverage();
            updateCpuHistory(load);
            if( highCpuUsage() || lowCpuUsage())
            {
                load = averageLoad();
                System.out.println("Writing Message");
                msg = msgFactory.notifyCpuUsage(load);;
                hostNode.write(msg);
            }
        }
    }

    private void updateCpuHistory(double load){
        if(cpuHistory.size() < Constants.VM_LOOK_BACK){
            cpuHistory.addLast(load);
        } else{
            cpuHistory.removeFirst();
            cpuHistory.addLast(load);
        }
    }

    private boolean highCpuUsage(){
        boolean all = true;
        for(Double d: cpuHistory){
            if(d < Constants.HIGH_CPU){
                all = false;
                break;
            }
        }
        return all;
    }

    private boolean lowCpuUsage(){
        boolean all = true;
        for(Double d: cpuHistory){
            if(d > Constants.LOW_CPU){
                all = false;
                break;
            }
        }
        return all;
    }

    private double averageLoad(){
        double load = 0;
        for(Double d: cpuHistory){
            load += d;
        }
        return load / cpuHistory.size();
    }

    /*
     * args[0] = hostNode ip
     * args[1] = hostNode socket
     *
     */
    public static void main( String [] args) throws Exception{
        if( args.length != 1)
            return;

        VMController runner = new VMController( args[0], Constants.NODE_PORT);
        runner.run();
    }
}
