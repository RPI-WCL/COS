import java.io.PrintWriter;
import java.net.Socket;
import java.lang.Thread;

public class NodeController
{
    public static void main( String [] args ) throws Exception
    {
        Socket host = new Socket("127.0.0.1", 9999);
        PrintWriter out = new PrintWriter(host.getOutputStream(), false);
        System.out.println("Set up. About to begin.");

        while(true)
        {
            System.out.println( args[0] );
           out.println(args[0]);
           Thread.sleep(500);
        }
    }
}
