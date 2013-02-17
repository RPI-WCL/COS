package util;

import common.Message;

import java.io.*;
import java.net.*;

public class CommChannel
{
    Socket m_sock;
    ObjectOutputStream out;
    ObjectInputStream in;

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

    public CommChannel( String addr, int port){
        try{
            m_sock = new Socket(addr, port);
            out = new ObjectOutputStream(m_sock.getOutputStream());
            in = new ObjectInputStream(m_sock.getInputStream());
        } catch(Exception e){ e.printStackTrace();}

    }

    public void write( Message message ){
        try{
            out.writeObject(message);
        }catch(Exception e){}
    }

    public Message read(){
        try{
            Message message = (Message) in.readObject();
            return message;
        }
        catch(Exception e){
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
}
