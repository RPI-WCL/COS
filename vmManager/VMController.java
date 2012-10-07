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

    public void run()
    {
        for( int i = 0; i < 25; i++)
        {
            double load = lambda.getWeightedSystemLoadAverage(); 
            String msg = Messages.notify_high_cpu_usage(load);
            hostNode.write(msg);
            try
            {
                Thread.sleep( 3000 );
            }
            catch(Exception e)
            {
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
