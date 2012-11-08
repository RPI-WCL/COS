package util;

import java.io.*;
import java.net.*;

public class CommChannel
{
    Socket m_sock;
    BufferedReader in;
    PrintWriter out;

    public CommChannel( Socket sock )
    {
        m_sock = sock;
        try
        {
            m_sock.setSoTimeout(50); //50 miliseconds
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(sock.getOutputStream(), true);
        }
        catch(SocketException s)
        {
            //TODO handle it.
        }
        catch(IOException io)
        {
            //TODO handle it.
        }
        
    }

    public CommChannel( String addr, int port)
    {
        try
        {
            m_sock = new Socket(addr, port);
            m_sock.setSoTimeout(50); //50 miliseconds
            in = new BufferedReader(new InputStreamReader(m_sock.getInputStream()));
            out = new PrintWriter(m_sock.getOutputStream(), true);
        }
        catch(UnknownHostException h)
        {  
            //TODO error out
        }
        catch(SocketException s)
        {
            //TODO handle it.
        }
        catch(IOException io)
        {
            //TODO handle it.
        }

    }

    public void write( String message )
    {
        out.println(message);
    }

    public String read()
    {
        if( m_sock.isClosed() )
            return null;
        String message = null;
        try
        {
            message = in.readLine();
            return message;
        }
        catch(IOException e)
        {
           return null; 
        }
    }

    public String getRemoteAddr()
    {
        return m_sock.getRemoteSocketAddress().toString();
    }

    public String getLocalAddr()
    {
        return m_sock.getLocalSocketAddress().toString();
    }

    public boolean isClosed()
    {
        return m_sock.isClosed();
    }
}
