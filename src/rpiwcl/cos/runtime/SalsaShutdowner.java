package rpiwcl.cos.runtime;

import java.io.*;
import java.util.*;
import org.ho.yaml.Yaml;
import wwc.messaging.ShutdownMessage;

public class SalsaShutdowner {

    public static void shutdown( String theater ) {
        String[] splits = theater.split( ":" );
        ShutdownMessage.send( splits[0], Integer.parseInt( splits[1] ) );
    }

    public static void main( String[] args ) {
        if (args.length != 1) {
            System.err.println( "Usage: java SalsShutdowner <runtimes.yaml>" );
        }

        HashMap runtimes = null;
        try {
            runtimes = (HashMap)Yaml.load(new File(args[0]));
        } catch (FileNotFoundException ex) {
            System.err.println( "File " + args[0] + " not found" );
        }

        ArrayList<String> runtimeIds = new ArrayList<String>();
        
        ArrayList rpiCloud = (ArrayList)runtimes.get( "cloud-rpiwcl" );
        // System.out.println( "rpiCloud: " + rpiCloud );
        for (int i = 0; i < rpiCloud.size(); i++) {
            HashMap map = (HashMap)rpiCloud.get( i );
            ArrayList<String> runtimeIdList = (ArrayList<String>)map.get( "runtimes" );
            if (runtimeIdList != null) {
                for (String runtimeId: runtimeIdList)
                    if (runtimeId != null)
                        runtimeIds.add( runtimeId );
            }
        }

        ArrayList ec2Cloud = (ArrayList)runtimes.get( "cloud-ec2" );
        for (int i = 0; i < ec2Cloud.size(); i++) {
            HashMap map = (HashMap)ec2Cloud.get( i );
            ArrayList<String> runtimeIdList = (ArrayList<String>)map.get( "runtimes" );
            if (runtimeIdList != null) {
                for (String runtimeId: runtimeIdList)
                    if (runtimeId != null)
                        runtimeIds.add( runtimeId );
            }
        }
            
        System.out.println( "Shutting down: " + runtimeIds );
        for (String runtimeId : runtimeIds)
            SalsaShutdowner.shutdown( runtimeId );
    }
}

        
