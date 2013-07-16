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
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.InstanceType;
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


public class AmazonEC2ApiStub {
    private static final int MAX_STATE_CHECK_RETRY_NUM = 10;
    private static final int STATE_CHECK_RETRY_INTERVAL = 3000; // [ms]

    private ArrayList<Instance> instances;
    private int fakeWaitsForStateChange = 0;


    public AmazonEC2ApiStub( String credentialFile, String securityGroup ) {
        instances = new ArrayList<Instance>();
        Instance instance = new Instance().
            withInstanceId( "i-56f88c37" ).
            withImageId( "ami-d0f89fb9" ).
            withState( new InstanceState().withName( "stopped" ) ).
            withPrivateIpAddress( "10.62.74.181" ).
            withPublicIpAddress( "54.226.132.88" ).
            withInstanceType( InstanceType.T1Micro ).
            withTags( new Tag( "Purpose", "COS" ) );

        instances.add( instance );

        System.out.println( "[EC2ApiStub] instances = " + instances );
    }


    public ArrayList<Instance> describeInstances() {
        if (instances == null) 
            System.err.println( "[EC2ApiStub] instances is NULL" );

        return instances;
    }


    public ArrayList<Instance> describeInstances( Collection<Filter> filters ) {
        return instances;
    }


    public ArrayList<Instance> describeInstances( ArrayList<String> instanceIds ) {
        return instances;
    }


    public ArrayList<Instance> runInstances( String instanceType,
                                             String imageId,
                                             String keyName,
                                             String zone,
                                             Integer count ) {
        for (Instance instance: instances)
            instance.setState( new InstanceState().withName( "pending" ) );

        return instances;
    }


    public ArrayList<InstanceStateChange> startInstances( ArrayList<String> instanceIds ) {
        ArrayList<InstanceStateChange> stateChanges = new ArrayList<InstanceStateChange>();

        for (String instanceId : instanceIds)
            stateChanges.add( new InstanceStateChange().
                              withCurrentState( new InstanceState().withName( "pending" ) ).
                              withPreviousState( new InstanceState().withName( "stopped" ) ).
                              withInstanceId( instanceId ) );
        return stateChanges;
    }


    public ArrayList<InstanceStateChange> stopInstances( ArrayList<String> instanceIds ) {
        ArrayList<InstanceStateChange> stateChanges = new ArrayList<InstanceStateChange>();

        for (String instanceId : instanceIds)
            stateChanges.add( new InstanceStateChange().
                              withCurrentState( new InstanceState().withName( "stopping" ) ).
                              withPreviousState( new InstanceState().withName( "running" ) ).
                              withInstanceId( instanceId ) );
        return stateChanges;
    }


    public ArrayList<InstanceStateChange> terminateInstances( ArrayList<String> instanceIds ) {
        ArrayList<InstanceStateChange> stateChanges = new ArrayList<InstanceStateChange>();

        for (String instanceId : instanceIds)
            stateChanges.add( new InstanceStateChange().
                              withCurrentState( new InstanceState().withName( "shutting-down" ) ).
                              withPreviousState( new InstanceState().withName( "running" ) ).
                              withInstanceId( instanceId ) );
        return stateChanges;
    }


    public void waitForStateChange( ArrayList<String> instanceIds, String targetState ) {
        double startTimeSec = (double)System.currentTimeMillis() / 1000;

        fakeWaitsForStateChange = 2;

        int retry = 0;
        boolean isTargetState = true;
        do {
            isTargetState = (0 == fakeWaitsForStateChange);

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
            fakeWaitsForStateChange--;
        } while (!isTargetState && (retry < MAX_STATE_CHECK_RETRY_NUM));

        double endTimeSec = (double)System.currentTimeMillis() / 1000;
        String result = (isTargetState) ? "SUCCESS" : "FAILED";
        System.out.println( "[EC2Api] waitForStateChange " + result + " after " +
                            (endTimeSec - startTimeSec) + "[sec]" );

        for (Instance instance: instances)
            instance.setState( new InstanceState().withName( "running" ) );
    }


    public void createTags( ArrayList<String> instanceIds, ArrayList<Tag> tags ) {
    }


    public static void main( String args[] ) {
        AmazonEC2ApiStub ec2Api = new AmazonEC2ApiStub( "AwsCredentials.properties", "quick-start-1" );
        boolean[] doTests = {true,     // 1. Test describeInstances();
                             true,     // 2. Test describeInstances( filters );
                             true,     // 3,4,5
                             false };  // 6. Terminate instances

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
