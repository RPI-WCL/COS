package rpiwcl.cos.runtime;

import wwc.messaging.ShutdownMessage;

public class SalsaShutdowner {

    public static void shutdown( String theater ) {
        String[] splits = theater.split( ":" );
        ShutdownMessage.send( splits[0], Integer.parseInt( splits[1] ) );
    }

    public static void main( String[] args ) {
        if (args.length != 1) {
            System.err.println( "Usage: java SalsaShutdowner.shutdown <theaterIpAddr:theaterPort>" );
            return;
        }

        SalsaShutdowner.shutdown( args[0] );
    }
}

        
