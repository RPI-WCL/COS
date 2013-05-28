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
import common.Usage;
import util.CommChannel;
import util.Utility;

public class VMController
{
    private CommChannel hostNode;
    private LinkedList<Usage> usageHistory;
    
    LinkedBlockingQueue<Message> mailbox;
    MessageFactory msgFactory;

    private VMController(String addr, int port){
        hostNode = new CommChannel(addr, port);
        mailbox = new LinkedBlockingQueue<Message>();
        usageHistory = new LinkedList<Usage>();
        ConnectionHandler listenLoop = new ConnectionHandler(hostNode, mailbox);
        new Thread(listenLoop, "Socket Listener").start();
        
        msgFactory = new MessageFactory("VM", hostNode);
    }

    private void createTheater(LinkedList<String> theaters){
        String launch_ios = "java -Dnetif=eth0 -Dconnection=\""
                          + theaters.getFirst() + "io/protocolActor\" "
                          + "-cp " + Constants.SALSA_PATH + ":" + Constants.IOS_PATH + " src.IOSTheater";
        //>/home/user/iosLog.txt &
        System.out.println(launch_ios);
        String use_script = "/home/user/launch_theater.sh " + theaters.getFirst();
        Process p;
        try{
            p = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", launch_ios});
        }
        catch(Exception e){
            e.printStackTrace();
            return;
        }
        /*
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
        */

        final BufferedReader input = new BufferedReader((new InputStreamReader(p.getInputStream())));
        final BufferedReader errorInput = new BufferedReader((new InputStreamReader(p.getErrorStream())));
        Runnable printOutput = new Runnable() {
            public void run() { 
                String s;
                try{
                    while(true) {
                        s = input.readLine();
                        if( s != null)
                            System.out.println(s);
                        s = errorInput.readLine();
                        if( s != null)
                            System.out.println(s);
                    }
                } catch( Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(printOutput, "Printing theater output").start();

    }

    private void handleMessage(Message msg){
        Message resp;
        Utility.debugPrint("VM recvd message: " + msg.getMethod());
        switch(msg.getMethod()) {
            case "shutdown_theater_request":
                //Runtime.getRuntime().exec("ls");
                break;
            case "start_theater":
                LinkedList<String> peers = (LinkedList<String>) msg.getParam("peers");
                createTheater(peers);
                break;
            case "get_usage":
                Usage average = new Usage(usageHistory);
                resp = msgFactory.usageResponse(average); 
                hostNode.write(resp);
                break;
            case "destroy_vm":
                //TODO: remove true so VM will actually shutdown.
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

    private void run() {
        Message msg;
        Usage usage;
        while(true) {
            try{
                msg = mailbox.poll(10L, TimeUnit.SECONDS);
                if( msg != null )
                    handleMessage(msg);
            } catch(InterruptedException e){
                e.printStackTrace();
            }

            updateUsageHistory();
            Usage average = new Usage(usageHistory);
            if(average.isExtremeUsage()) {
                msg = msgFactory.notifyExtremeUsage(average);
                hostNode.write(msg);
            }
        }
    }

    private void updateUsageHistory(){
        Usage usage = new Usage();
        if(usageHistory.size() < Constants.VM_LOOK_BACK){
            usageHistory.addLast(usage);
        } else{
            usageHistory.removeFirst();
            usageHistory.addLast(usage);
        }
    }

    /*
     * args[0] = hostNode ip
     * args[1] = hostNode socket
     */
    public static void main( String [] args) throws Exception{
        if( args.length != 1)
            return;

        VMController runner = new VMController( args[0], Constants.NODE_PORT);
        runner.run();
    }
}
