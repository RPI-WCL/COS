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

public class COSController
{
    public static void main(String[] args) throws Exception
    {
        ServerSocketChannel listenerChannel = ServerSocketChannel.open();
        ServerSocket listener = listenerChannel.socket();
        listener.bind( new InetSocketAddress(9999));
        //listenerChannel.configureBlocking(false);

        Selector selector = Selector.open();
        if( listenerChannel.validOps() != SelectionKey.OP_ACCEPT)
        {
            System.out.println("Something is wrong.");
            return;
        }
        listenerChannel.register(selector, listenerChannel.validOps());
        System.out.println("Preparing to listen!");
        while(true)
        {
            //System.out.println("I want messages!");
            Iterator<SelectionKey> it =  selector.selectedKeys().iterator(); 

            System.out.println( Integer.toString( selector.select() ));

            while(it.hasNext())
            {
                //System.out.println("I have messages!");
                SelectionKey selkey = it.next();
                it.remove();

                if( !selkey.isValid())
                {
                    System.out.println("Key isn't valid");
                    continue;
                }

                if( selkey.isAcceptable() )
                {
                    System.out.println("Incoming connection");
                    Socket newsock = listener.accept();
                    SocketChannel newsockc = newsock.getChannel();
                    //newsockc.configureBlocking(false);
                    newsockc.register(selector, SelectionKey.OP_READ);
                }
                else if( selkey.isReadable() )
                {
                    System.out.println("Reading from connection");
                    SocketChannel sc =(SocketChannel) selkey.channel();
                    BufferedReader in = new BufferedReader( new InputStreamReader(
                                                            sc.socket().getInputStream()));
                    //ByteBuffer alpha = ByteBuffer.allocateDirect(1024);
                    //sc.read(alpha);
                    //Charset charset = Charset.defaultCharset();
                    //CharsetDecoder decoder = charset.newDecoder();
                    //String message = decoder.decode(alpha).toString();
                    String message = in.readLine();
                    System.out.println(message);
                }
                else
                {
                    System.out.println("Other");
                }
            }
        }
    }
}

