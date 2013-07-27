package rpiwcl.cos.cloud;

import com.amazonaws.services.ec2.*;
import com.amazonaws.services.ec2.model.*;
import java.io.*;
import java.util.*;
import org.ho.yaml.Yaml;
import rpiwcl.cos.cloud.*;
import rpiwcl.cos.runtime.*;


public class AmazonEC2InstancesTerminator {
    private final static String AWS_CREDENTIAL = "AwsCredentials.properties";
    private final static String EC2_SECURITY_GROUP = "quick-start-1";

    public static void main( String[] args ) {
        if (args.length != 1) {
            System.err.println( "Usage: java AmazonEC2TerminateInstances <runtimes.txt>" );
        }

        ArrayList<String> instanceIds = new ArrayList<String>();

		try {
			BufferedReader in = new BufferedReader( new FileReader( args[0] ) );
            String str = null;
            in.readLine(); // skip the first line
            do {
                if ((str = in.readLine()) != null) {
                    String[] splits = str.split( "," );
                    if (!splits[2].equals( "null" ))
                        instanceIds.add( splits[2] );
                }
            } while (str != null);

			in.close(); 
		} catch ( IOException ex ) {
			System.err.println( "Error: Can't open the file " + args[0] + " for reading." );
		}

        AmazonEC2Api ec2 = new AmazonEC2Api( AWS_CREDENTIAL, EC2_SECURITY_GROUP );
        System.out.println( "[AmazonEC2] Terminating: " + instanceIds );
        ec2.terminateInstances( instanceIds );
    }
}
