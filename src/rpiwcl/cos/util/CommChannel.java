package rpiwcl.cos.util;

import java.io.*;
import java.net.*;
import java.net.InetSocketAddress;

import rpiwcl.cos.common.Message;
import rpiwcl.cos.util.Utility;

public class CommChannel
{
    Socket m_sock;
    ObjectOutputStream out;
    ObjectInputStream in;

    private static final int SOCKET_CONNECT_TIMEOUT = 3000; // [ms]

    public CommChannel( Socket sock ){
        m_sock = sock;
        try{
            out = new ObjectOutputStream(sock.getOutputStream());
            in = new ObjectInputStream(sock.getInputStream());
        }
        catch(Exception s){
            s.printStackTrace();
        }
    }

    public CommChannel( String addr, int port) throws IOException {
        InetSocketAddress sockAddr = new InetSocketAddress( addr, port );
        m_sock = new Socket();
        m_sock.connect( sockAddr, SOCKET_CONNECT_TIMEOUT );
        out = new ObjectOutputStream(m_sock.getOutputStream());
        in = new ObjectInputStream(m_sock.getInputStream());
    }

    public void write( Message message ){
        try{
            Utility.debugPrint( "[CommChannel] Sending " + message );
            out.writeObject(message);
        }catch(Exception e){
            System.err.println( "ERROR!!" + e );
        }
    }

    public Message read(){
        try{
            Message message = (Message) in.readObject();
            return message;
        }
        catch(Exception e){
           e.printStackTrace();
           return null; 
        }
    }

    public String getRemoteAddr(){
        return m_sock.getRemoteSocketAddress().toString();
    }

    public String getLocalAddr(){
        return m_sock.getLocalSocketAddress().toString();
    }

    public boolean isClosed(){
        return m_sock.isClosed();
    }

    public String toString() {
        return m_sock.toString();
    }
}
