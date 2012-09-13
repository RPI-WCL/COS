import java.io.*;
import java.lang.System;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.LinkedList;

public class COSController
{
    public static void main(String[] args) throws Exception
    {
        ServerSocket listener = new ServerSocket(9999, 25);
        listener.setSoTimeout(5000);
        LinkedList<Socket> sockets = new LinkedList<Socket>();
        BufferedReader in = null;

        System.out.println( "Beginning to listen" );
        while(true)
        {
            Socket newsock = null;
            try
            {
                newsock = listener.accept();
            }
            catch(Exception e)
            {
                //Don't worry
            }
            if( newsock != null)
            {
                newsock.setSoTimeout(5000);
                sockets.add(newsock);
            }

            for( Socket sock : sockets )
            {
                in = new BufferedReader( new InputStreamReader( sock.getInputStream()));
                String message = null;
                try
                {
                    message = in.readLine();
                    System.out.println(message); 
                }
                catch(Exception e)
                {
                }
            }
        }
    }
}

