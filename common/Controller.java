package common;

import java.io.*;
import java.lang.Thread;
import java.net.*;
import java.util.LinkedList;

import util.CommChannel;

public abstract class Controller
{
    private ServerSocket listener;
    private LinkedList<CommChannel> sockets;
    abstract public void handleMessage(String message, CommChannel sock);
    abstract public void periodic();

    public Controller(int port) 
    {
        try
        {
            listener = new ServerSocket(port, 64);
            listener.setSoTimeout(32);
            sockets = new LinkedList<CommChannel>();
        }
        catch( SocketException s)
        {
            //Should log this.
            //
        }
        catch( IOException e )
        {
            //Should log this.
            //
        }
    }

    public void checkMessages()
    {
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
                sockets.add( new CommChannel(newsock));
            }

            for( CommChannel sock : sockets )
            {
                if( sock.isClosed() )
                    continue;
                String message = sock.read();
                if( message != null)
                {
                    handleMessage(message, sock);
                }
            }
            periodic();
            try
            {
                //Avoid busy polling.
                Thread.sleep(3000);
            }
            catch(Exception e)
            {
                //Don't care if we get interrupted from sleeping
            }
        }
    }
}
