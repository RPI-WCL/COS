package vmManager;

import java.io.*;
import java.util.*;
import java.net.Socket;
import java.lang.Thread;

import util.CommChannel;
import util.Messages;
import util.Lambda;

public class VMController
{
    CommChannel hostNode;
    Lambda lambda;
    Messages msgHandler;

    final String salsaPath = "/home/user/salsa/salsa0.7.2.jar";
    final String iosPath = "/home/user/ios/ios0.4.jar";
    final String launchIOS = "java -cp " + salsaPath + ":" + iosPath +
                             " src.testing.reachability.Full theaters.txt";


    public VMController(String addr, int port)
    {
        hostNode = new CommChannel(addr, port);
        lambda = Lambda.getInstance();
        msgHandler = new Messages(hostNode);
    }

    public void createTheater(Iterable<String> theaters)
    {
        try
        {
            FileWriter fstream = new FileWriter("theaters.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            for( String s: theaters)
                out.write( theaters + "\n" );
            out.close();

            
            Runtime.getRuntime().exec(launchIOS);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void handleMsg(String msg)
    {
        Lambda.debugPrint("VM recvd message: " + msg);
        String resp;
        switch(msgHandler.get_request_type(msg))
        {
            case "shutdown_theater_request":

                //Runtime.getRuntime().exec("ls");
                break;
            case "create_theater":
                List<String> theaters = msgHandler.get_params(msg);
                createTheater(theaters);
                break;
            case "get_cpu_usage":
                double load = lambda.getWeightedSystemLoadAverage();
                resp = msgHandler.notify_vm_cpu_usage(load);
                hostNode.write(resp);
                break;
                
            case "shutdown_request":
                try
                {
                    Runtime.getRuntime().exec("shutdown -h now");
                }
                catch(IOException e)
                {
                    //Report out to Node Controller. Tell it to destroy me.
                }
                break;
        }
    }

    public void run()
    {
        double load;
        String msg;
        while(true)
        {
            msg = hostNode.read();
            //We just got a letter!
            if(msg != null)
            {
                handleMsg(msg);
            }

            load = lambda.getWeightedSystemLoadAverage();
            if( load < .25 )
            {
                msg = msgHandler.notify_low_cpu_usage(load);
                hostNode.write(msg);

            }
            else if( load > .75 )
            {
                msg = msgHandler.notify_high_cpu_usage(load);
                hostNode.write(msg);
            }

            try
            {
                Thread.sleep( 10000 );
            }
            catch(Exception e)
            {
                //Who cares if we get interrupted?
            }
        }
    }

    /*
     * args[0] = hostNode ip
     * args[1] = hostNode socket
     *
     */
    public static void main( String [] args) throws Exception
    {
        if( args.length != 2)
            return;

        VMController runner = new VMController( args[0], Integer.parseInt(args[1]));
        runner.run();
    }
}
