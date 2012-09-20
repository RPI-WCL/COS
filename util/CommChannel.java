package util;

import java.io.*;

public class CommChannel
{
    Socket m_sock;
    BufferedReader in;
    PrintWriter out;

    CommChannel( Socket sock )
    {
        m_sock = sock;
        m_sock.setSoTimeout(50); //50 miliseconds
        in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        out = new PrintWriter(sock.getOutputStream(), true);
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
            message = in.readline();
        }
        catch()
        {
        }
    }
}
