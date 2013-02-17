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
    LinkedBlockingQueue<Message> mailbox;
    MessageFactory msgFactory;

    final String salsaPath = "/home/user/salsa/salsa0.7.2.jar";
    final String iosPath = "/home/user/ios/ios0.4.jar";
    final String launchIOS = "java -cp " + salsaPath + ":" + iosPath +
                             " src.testing.reachability.Full theaters.txt";

    public VMController(String addr, int port){
        hostNode = new CommChannel(addr, port);
        mailbox = new LinkedBlockingQueue<Message>();
        ConnectionHandler listenLoop = new ConnectionHandler(hostNode, mailbox);
        new Thread(listenLoop, "Socket Listener").start();
        
        msgFactory = new MessageFactory("VM", hostNode);
    }

    public void createTheater(Iterable<String> theaters){
        try{
            FileWriter fstream = new FileWriter("theaters.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            for( String s: theaters)
                out.write( theaters + "\n" );
            out.close();

            Runtime.getRuntime().exec(launchIOS);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void handleMessage(Message msg){
        Utility.debugPrint("VM recvd message: " + msg.getMethod());
        Message resp;
        switch(msg.getMethod())
        {
            case "shutdown_theater_request":

                //Runtime.getRuntime().exec("ls");
                break;
//            This needs to be redone with the new messages
//            case "create_theater":
//                List<String> theaters = msgHandler.get_params(msg);
//                createTheater(theaters);
//                break;
            case "get_cpu_usage":
                double load = Utility.getWeightedSystemLoadAverage();
                resp = msgFactory.cpuUsageResp(load); 
                hostNode.write(resp);
                break;
                
            case "shutdown_request":
                try{
                    Runtime.getRuntime().exec("shutdown -h now");
                } catch(IOException e){
                    //Report out to Node Controller. Tell it to destroy me.
                }
                break;
        }
    }

    public void run()
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
            System.out.println("Checking if should write message");
            System.out.println("Load is " + load);
            if( load > Constants.HIGH_CPU || load < Constants.LOW_CPU || true )
            {
                System.out.println("Writing Message");
                msg = msgFactory.notifyCpuUsage(load);;
                hostNode.write(msg);
            }
        }
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
