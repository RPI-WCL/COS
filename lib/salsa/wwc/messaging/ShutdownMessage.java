package wwc.messaging;

import java.io.*;
import java.net.*;

public class ShutdownMessage implements Serializable {

    public static void send( String host, int port ) {

        try {
            Socket sock = new Socket();
            sock.connect( new InetSocketAddress( host, port ) );

            BufferedOutputStream bos = 
                new BufferedOutputStream( sock.getOutputStream() );
            ObjectOutputStream oos = new ObjectOutputStream( bos );

            oos.writeObject( new ShutdownMessage() );
            oos.flush();

            oos.close();
            bos.close();
            sock.close();
        } catch (IOException ex) {
            System.err.println( ex );
        }
    }
}
        
