package util;

import java.io.*;
import java.net.*;

public class CommChannel
{
    Socket m_sock;
    BufferedReader in;
    PrintWriter out;

    CommChannel( Socket sock )
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

    void write( String message )
    {
        out.println(message);
    }

    String read()
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
}
