package common;

import java.io.*;
import java.lang.Thread;
import java.net.*;
import java.util.*;

import util.CommChannel;
import cosManager.ConStat;

public abstract class Controller
{
    private ServerSocket listener;
    protected LinkedList<CommChannel> sockets;
    protected HashMap<CommChannel, ConStat> socketStats;
    abstract public void droppedHook(CommChannel dropped);
    abstract public void newHook(CommChannel newbie);
    abstract public void handleMessage(String message, CommChannel sock);
    abstract public void periodic();

    public Controller(int port) 
    {
        try
        {
            listener = new ServerSocket(port, 64);
            listener.setSoTimeout(32);
            sockets = new LinkedList<CommChannel>();
            socketStats = new HashMap<CommChannel, ConStat>();
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
        LinkedList<CommChannel> dead = new LinkedList<CommChannel>();
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
                CommChannel newbie = new CommChannel(newsock);
                sockets.add(newbie);
                newHook(newbie);
            }

            for( CommChannel sock : sockets )
            {
                if( sock.isClosed() )
                {
                    droppedHook(sock);
                    dead.push(sock);
                    continue;
                }
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
                Thread.sleep(300);
            }
            catch(Exception e)
            {
                //Don't care if we get interrupted from sleeping
            }
            
            //Cleanup dead sockets
            for(CommChannel sock : dead)
            {
                sockets.remove(sock);
            }
            dead.clear();
        }
    }
}
