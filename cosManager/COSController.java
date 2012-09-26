package cosManager;

import java.io.*;
import java.lang.System;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

import common.Controller;
import util.CommChannel;

public class COSController extends Controller
{
    public COSController()
    {
        super(9999);
    }

    public void handleMessage( String message, CommChannel sock)
    {
        System.out.println(message);
    }

    public static void main(String[] args) throws Exception
    {
//        ServerSocket listener = new ServerSocket(9999, 25);
//        listener.setSoTimeout(50);
        //LinkedList<Socket> sockets = new LinkedList<Socket>();
//        LinkedList<CommChannel> sockets = new LinkedList<CommChannel>();
        COSController runner = new COSController();
        runner.checkMessages();

//        System.out.println( "Beginning to listen" );
//        while(true)
//        {
//            Socket newsock = null;
//            try
//            {
//                newsock = listener.accept();
//            }
//            catch(Exception e)
//            {
//                //Don't worry
//            }
//            if( newsock != null)
//            {
//                newsock.setSoTimeout(50);
//                sockets.add(newsock);
//                sockets.add( new CommChannel(newsock));
//            }
//
//            for( CommChannel sock : sockets )
//            {
//                if( sock.isClosed() )
//                    continue;
//                String message = sock.read();
//                if( message != null)
//                {
//                    System.out.println(message);
//                }
//            }
//        }
    }
}

