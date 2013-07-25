package rpiwcl.cos.cloud;

import com.amazonaws.services.ec2.*;
import com.amazonaws.services.ec2.model.*;
import java.io.*;
import java.util.*;
import org.ho.yaml.Yaml;
import rpiwcl.cos.cloud.*;
import rpiwcl.cos.runtime.*;


public class AmazonEC2TerminateInstances {
    private final static String AWS_CREDENTIAL = "AwsCredentials.properties";
    private final static String EC2_SECURITY_GROUP = "quick-start-1";

    public static void main( String[] args ) {
        if (args.length != 1) {
            System.err.println( "Usage: java AmazonEC2TerminateInstances <runtimes.yaml>" );
        }

        HashMap conf = null;
        try {
            conf = (HashMap)Yaml.load(new File(args[0]));
        } catch (FileNotFoundException ex) {
            System.err.println( "File " + args[0] + " not found" );
        }

        ArrayList<String> instanceIds = new ArrayList<String>();
        
        ArrayList ec2Cloud = (ArrayList)conf.get( "cloud-ec2" );
        for (int i = 0; i < ec2Cloud.size(); i++) {
            HashMap map = (HashMap)ec2Cloud.get( i );
            ArrayList<String> instanceIdList = (ArrayList<String>)map.get( "instanceIds" );
            if (instanceIdList != null) {
                for (String instanceId: instanceIdList)
                    if (instanceId != null)
                        instanceIds.add( instanceId );
            }
        }


        AmazonEC2Api ec2 = new AmazonEC2Api( AWS_CREDENTIAL, EC2_SECURITY_GROUP );
        System.out.println( "[AmazonEC2] Terminating: " + instanceIds );
        ec2.terminateInstances( instanceIds );
    }
}
