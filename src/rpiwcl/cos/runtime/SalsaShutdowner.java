package rpiwcl.cos.runtime;

import java.io.*;
import java.util.*;
import wwc.messaging.ShutdownMessage;

public class SalsaShutdowner {

    public static void shutdown( String theater ) {
        String[] splits = theater.split( ":" );
        ShutdownMessage.send( splits[0], Integer.parseInt( splits[1] ) );
    }

    public static void main( String[] args ) {
        if (args.length != 1) {
            System.err.println( "Usage: java SalsaShutdowner <runtimes.txt>" );
        }

		try {
			BufferedReader in = new BufferedReader( new FileReader( args[0] ) );
            String str = null;
            in.readLine(); // skip the first line
            do {
                if ((str = in.readLine()) != null) {
                    String[] splits = str.split( "," );
                    System.out.println( "Shutting down: " + splits[1] );
                    SalsaShutdowner.shutdown( splits[1] );
                }
            } while (str != null);

			in.close(); 
		} catch ( IOException ex ) {
			System.err.println( "Error: Can't open the file " + args[0] + " for reading." );
		}
    }
}

        
