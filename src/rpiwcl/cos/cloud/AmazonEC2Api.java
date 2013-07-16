package rpiwcl.cos.cloud;

import java.io.IOException;
import java.util.*;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;


public class AmazonEC2Api {
    private AmazonEC2 ec2;
    private String securityGroup;

    private static final int MAX_STATE_CHECK_RETRY_NUM = 15;
    private static final int STATE_CHECK_RETRY_INTERVAL = 20000; // [ms]


    public AmazonEC2Api( String credentialFile, String securityGroup ) {
        AWSCredentials credentials = null;

        try {
             credentials = new PropertiesCredentials(
                 AmazonEC2Api.class.getResourceAsStream( credentialFile ) );
        } catch (IOException ex) {
            System.err.println( ex );
        }

        try {
            ec2 = new AmazonEC2Client( credentials );
            Region usEast = Region.getRegion( Regions.US_EAST_1 );
            ec2.setRegion( usEast );

            this.securityGroup = securityGroup;

            System.out.println( "[EC2Api] Created " + ec2 );

        } catch (AmazonServiceException ex) {
            System.err.println( ex );
        }
    }


    public ArrayList<Instance> describeInstances() {
        ArrayList<Instance> instances = new ArrayList<Instance>();

        // Retrieve ALL instance information associated with the credential
        try {
            DescribeInstancesResult result = ec2.describeInstances();

            List<Reservation> reservations = result.getReservations();
            for (Reservation reservation : reservations) {
                List<Instance> instances_ = reservation.getInstances();
                for (Instance instance : instances_)
                    instances.add( instance );
            }

        } catch (AmazonServiceException ex) {
            System.err.println( ex );
        } finally {
            return instances;
        }
    }

    public ArrayList<Instance> describeInstances( Collection<Filter> filters ) {
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.setFilters( filters );

        ArrayList<Instance> instances = new ArrayList<Instance>();
        try {
            DescribeInstancesResult result = ec2.describeInstances( request );

            List<Reservation> reservations = result.getReservations();
            for (Reservation reservation : reservations) {
                List<Instance> instances_ = reservation.getInstances();
                for (Instance instance : instances_)
                    instances.add( instance );
            }

        } catch (AmazonServiceException ex) {
            System.err.println( ex );
        } finally {
            return instances;
        }
    }


    public ArrayList<Instance> describeInstances( ArrayList<String> instanceIds ) {
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.setInstanceIds( instanceIds );

        // Retrieve instance information
        ArrayList<Instance> instances = new ArrayList<Instance>();
        try {
            DescribeInstancesResult result = ec2.describeInstances( request );

            List<Reservation> reservations = result.getReservations();
            for (Reservation reservation : reservations) {
                List<Instance> instances_ = reservation.getInstances();
                for (Instance instance : instances_)
                    instances.add( instance );
            }

        } catch (AmazonServiceException ex) {
            System.err.println( ex );
        } finally {
            return instances;
        }
    }


    public ArrayList<Instance> runInstances( String instanceType,
                                             String imageId,
                                             String keyName,
                                             String zone,
                                             Integer count ) {
        RunInstancesRequest request = new RunInstancesRequest();
        request.setInstanceType( instanceType );
        request.setImageId( imageId );
        request.setKeyName( keyName );
        request.setPlacement( new Placement( zone ) );
        request.setMinCount( count );
        request.setMaxCount( count );
    	ArrayList<String> securityGroups = new ArrayList<String>();
    	securityGroups.add( this.securityGroup );
        request.setSecurityGroups( securityGroups );

        // Create the new instance
        ArrayList<Instance> instances = new ArrayList<Instance>();
        try {
            RunInstancesResult result = ec2.runInstances( request );
            Reservation reservation = result.getReservation();

            List<Instance> instances_ = reservation.getInstances();
            for (Instance instance : instances_) 
                instances.add( instance );

        } catch (AmazonServiceException ex) {
            System.err.println( ex );
        } finally {
            return instances;
        }
    }


    public ArrayList<InstanceStateChange> startInstances( ArrayList<String> instanceIds ) {
        StartInstancesRequest request = new StartInstancesRequest( instanceIds );
        ArrayList<InstanceStateChange> stateChanges = new ArrayList<InstanceStateChange>();

        // Start the stopped instance
        try {
            StartInstancesResult result = ec2.startInstances( request );
            java.util.List<InstanceStateChange> stateChanges_ = result.getStartingInstances();

            for (InstanceStateChange sc : stateChanges_)
                stateChanges.add( sc );

        } catch (AmazonServiceException ex) {
            System.err.println( ex );
        } finally {
            return stateChanges;
        }
    }


    public ArrayList<InstanceStateChange> stopInstances( ArrayList<String> instanceIds ) {
        StopInstancesRequest request = new StopInstancesRequest( instanceIds );
        ArrayList<InstanceStateChange> stateChanges = new ArrayList<InstanceStateChange>();

        // Stop the started instance
        try {
            StopInstancesResult result = ec2.stopInstances( request );
            java.util.List<InstanceStateChange> stateChanges_ = result.getStoppingInstances();

            for (InstanceStateChange sc : stateChanges_) {
                stateChanges.add( sc );
            }

        } catch (AmazonServiceException ex) {
            System.err.println( ex );
        } finally {
            return stateChanges;
        }
    }


    public ArrayList<InstanceStateChange> terminateInstances( ArrayList<String> instanceIds ) {
        TerminateInstancesRequest request = new TerminateInstancesRequest( instanceIds );
        ArrayList<InstanceStateChange> stateChanges = new ArrayList<InstanceStateChange>();

        // Terminate the started instance
        try {
            TerminateInstancesResult result = ec2.terminateInstances( request );
            java.util.List<InstanceStateChange> stateChanges_ = result.getTerminatingInstances();

            for (InstanceStateChange sc : stateChanges_) {
                stateChanges.add( sc );
            }

        } catch (AmazonServiceException ex) {
            System.err.println( ex );
        } finally {
            return stateChanges;
        }
    }


    public void waitForStateChange( ArrayList<String> instanceIds, String targetState ) {
        double startTimeSec = (double)System.currentTimeMillis() / 1000;

        int retry = 0;
        boolean isTargetState = true;
        do {
            ArrayList<Instance> instances = describeInstances( instanceIds );

            isTargetState = true;
            System.out.print( "[EC2Api] ");
            for (Instance instance : instances) {
                System.out.print( instance.getInstanceId() + ": " + 
                                  instance.getState().getName() + " " );
                if( !instance.getState().getName().equals( targetState ) ||
                    (instance.getPublicIpAddress() == null) ) {
                    isTargetState = false;
                    break;
                }
            }
            System.out.println();

            if (!isTargetState) {
                System.err.println( "[EC2Api] " + instanceIds + 
                                    " have not transitioned to " + targetState +
                                    ", retry=" + ++retry );
                try {
                    Thread.sleep( STATE_CHECK_RETRY_INTERVAL );
                } catch (InterruptedException ie) {
                    System.err.println( ie );
                }
            }
            else {
                System.out.println( "[EC2Api] " + instanceIds + 
                                    " have transitioned to " + targetState );
            }
        } while (!isTargetState && (retry < MAX_STATE_CHECK_RETRY_NUM));

        double endTimeSec = (double)System.currentTimeMillis() / 1000;
        String result = (isTargetState) ? "SUCCESS" : "FAILED";
        System.out.println( "[EC2Api] waitForStateChange " + result + " after " +
                            (endTimeSec - startTimeSec) + "[sec]" );
    }


    public void createTags( ArrayList<String> instanceIds, ArrayList<Tag> tags ) {
        CreateTagsRequest request = new CreateTagsRequest( instanceIds, tags );
        try {
            ec2.createTags( request );
        } catch (AmazonServiceException ex) {
            System.err.println( ex );
        }
    }


    public static void main( String args[] ) {
        AmazonEC2Api ec2Api = new AmazonEC2Api( "AwsCredentials.properties", "quick-start-1" );
        boolean[] doTests = {false,     // 1. Test describeInstances();
                             true,      // 2. Test describeInstances( filters );
                             true,      // 3,4,5
                             false };   // 6. Terminate instances

        // 1. Test describeInstances();
        if (doTests[0]) {
            System.out.println( "########## Test describeInstances()" );
            ArrayList<Instance> instances1 = ec2Api.describeInstances();
            for (Instance instance : instances1) {
                System.out.println( instance );
            }
            System.out.println();
        }

        // 2. Test describeInstances( filters );
        if (doTests[1]) {
            System.out.println( "########## Test describeInstances( filters )" );
            ArrayList<Filter> filters = new ArrayList<Filter>();
            Filter filter = new Filter().withName( "tag:Purpose" ).withValues( "COS" );
            // Filter filter = new Filter().withName( "instance-state-name" ).
            //     withValues( "running" );
            // Filter filter = new Filter().withName( "instance-type" ).
            //     withValues( "t1.micro" );
            System.out.println( filter );
            filters.add( filter );
            ArrayList<Instance> instances2 = ec2Api.describeInstances( filters );
            for (Instance instance : instances2) {
                System.out.println( instance );
            }
            System.out.println();
        }

        if (doTests[2]) {
            // 3. Test runInstances
            System.out.println( "########## Test runInstances" );
            ArrayList<Instance> newInstances = 
                ec2Api.runInstances( "m1.small", "ami-4a582623", "wclcloud", "us-east-1d", 1 );
            ArrayList<String> instanceIds = new ArrayList<String>();
            for (Instance instance : newInstances) {
                instanceIds.add( instance.getInstanceId() );
                System.out.println( instance );
            }
            System.out.println();

            // 4. Test waitForStateChange
            System.out.println( "########## Test waitForStateChange" );
            ec2Api.waitForStateChange( instanceIds, "running" );
            ArrayList<Instance> instances3 = ec2Api.describeInstances( instanceIds );
            for (Instance instance : instances3) {
                instanceIds.add( instance.getInstanceId() );
                System.out.println( instance );
            }
            System.out.println();

            // 5. Test createTags
            System.out.println( "########## Test createTags" );
            ArrayList<Tag> tags = new ArrayList<Tag>();
            tags.add( new Tag( "Purpose", "COS" ) );
            ec2Api.createTags( instanceIds, tags );
            ArrayList<Instance> instances4 = ec2Api.describeInstances( instanceIds );
            for (Instance instance : instances4)
                System.out.println( instance );
            System.out.println();
        }

        // 6. Terminate instances
        if (doTests[3]) {
            System.out.println( "########## Test terminateInstances" );
            ArrayList<String> instanceIds5 = new ArrayList<String>();
            instanceIds5.add( "i-44fff72a" );
            ArrayList<InstanceStateChange> stateChanges = 
                ec2Api.terminateInstances( instanceIds5 );
            for (InstanceStateChange sc : stateChanges)
                System.out.println( sc );
            System.out.println();
        }
    }
}
