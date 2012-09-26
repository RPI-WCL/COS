package nodeManager;

import java.io.PrintWriter;
import java.net.Socket;
import java.lang.Thread;

import util.CommChannel;

public class NodeController
{
    public static void main( String [] args ) throws Exception
    {
        Socket host = new Socket("127.0.0.1", 9999);
        //PrintWriter out = new PrintWriter(host.getOutputStream(), true);
        CommChannel chan = new CommChannel(host);
        System.out.println("Set up. About to begin.");

        for(int i =0; i < 10; i++)
        {
            System.out.println( args[0] );
            chan.write(args[0]);
            Thread.sleep(500);
        }
        Thread.sleep(100);
    }
}
