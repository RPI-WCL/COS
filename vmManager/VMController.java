package vmManager;

import java.net.Socket;
import java.lang.Thread;

import util.CommChannel;
import util.Messages;
import util.Lambda;

public class VMController
{
    CommChannel hostNode;
    Lambda lambda;

    public VMController(String addr, int port)
    {
        hostNode = new CommChannel(addr, port);
        lambda = Lambda.getInstance();
    }

    public void handleMsg(String msg)
    {
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
            if( load < .1 )
            {
                msg = Messages.notify_low_cpu_usage(load);
                hostNode.write(msg);

            }
            else if( load > .9 )
            {
                msg = Messages.notify_high_cpu_usage(load);
                hostNode.write(msg);
            }

            try
            {
                Thread.sleep( 3000 );
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
