package rpiwcl.cos.cloud.pubcloud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;

import rpiwcl.cos.cloud.pubcloud.*;


public class AmazonEC2Api implements CloudApi {
    private AmazonEC2 ec2;
    private String securityGroup;

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

            System.out.println( "Created " + ec2 );

        } catch (AmazonServiceException ex) {
            System.err.println( ex );
        }
    }


    public ArrayList<VmInstance> getAllInstances() {
        ArrayList<VmInstance> vmInstances = new ArrayList<VmInstance>();

        // Retrieve ALL instance information associated with the credential
        try {
            DescribeInstancesResult result = ec2.describeInstances();

            List<Reservation> reservations = result.getReservations();
            for (Reservation reservation : reservations) {
                List<Instance> instances = reservation.getInstances();
                for (Instance instance : instances)
                    vmInstances.add( new AmazonEC2VmInstance( instance ) );
            }

        } catch (AmazonServiceException ex) {
            System.err.println( ex );
        } finally {
            return vmInstances;
        }
    }


    public ArrayList<VmInstance> getInstances( ArrayList<String> instanceIds ) {
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.setInstanceIds( instanceIds );

        // Retrieve instance information
        ArrayList<VmInstance> vmInstances = new ArrayList<VmInstance>();
        try {
            DescribeInstancesResult result = ec2.describeInstances( request );

            List<Reservation> reservations = result.getReservations();
            for (Reservation reservation : reservations) {
                List<Instance> instances = reservation.getInstances();
                for (Instance instance : instances)
                    vmInstances.add( new AmazonEC2VmInstance( instance ) );
            }

        } catch (AmazonServiceException ex) {
            System.err.println( ex );
        } finally {
            return vmInstances;
        }
    }


    /* NOT TESTED */
    public ArrayList<VmInstance> createInstances( String instanceType,
                                                  String imageId,
                                                  Integer count,
                                                  ArrayList<String> instanceIds /*OUT*/ ) {
        RunInstancesRequest request = new RunInstancesRequest();
        request.setInstanceType( instanceType );
        request.setImageId( imageId );
        request.setMinCount( count );
        request.setMaxCount( count );
    	ArrayList<String> securityGroups = new ArrayList<String>();
    	securityGroups.add( this.securityGroup );
        request.setSecurityGroups( securityGroups );

        // Create the new instance
        ArrayList<VmInstance> vmInstances = new ArrayList<VmInstance>();
        try {
            RunInstancesResult result = ec2.runInstances( request );
            Reservation reservation = result.getReservation();

            List<Instance> instances = reservation.getInstances();
            for (Instance instance : instances) 
                vmInstances.add( new AmazonEC2VmInstance( instance ) );

        } catch (AmazonServiceException ex) {
            System.err.println( ex );
        } finally {
            return vmInstances;
        }
    }


    public ArrayList<VmInstanceStateChange> startInstances( ArrayList<String> instanceIds ) {
        StartInstancesRequest request = new StartInstancesRequest( instanceIds );
        ArrayList<VmInstanceStateChange> vmStateChanges = new ArrayList<VmInstanceStateChange>();

        // Start the stopped instance
        try {
            StartInstancesResult result = ec2.startInstances( request );
            java.util.List<InstanceStateChange> stateChanges = result.getStartingInstances();

            for (InstanceStateChange sc : stateChanges) {
                VmInstanceStateChange stateChange = 
                    new VmInstanceStateChange( sc.getInstanceId(), 
                                               sc.getCurrentState().getName(), 
                                               sc.getPreviousState().getName() );
                vmStateChanges.add( stateChange );
            }

        } catch (AmazonServiceException ex) {
            System.err.println( ex );
        } finally {
            return vmStateChanges;
        }
    }


    public ArrayList<VmInstanceStateChange> stopInstances( ArrayList<String> instanceIds ) {
        StopInstancesRequest request = new StopInstancesRequest( instanceIds );
        ArrayList<VmInstanceStateChange> vmStateChanges = new ArrayList<VmInstanceStateChange>();

        // Stop the started instance
        try {
            StopInstancesResult result = ec2.stopInstances( request );
            java.util.List<InstanceStateChange> stateChanges = result.getStoppingInstances();

            for (InstanceStateChange sc : stateChanges) {
                VmInstanceStateChange stateChange = 
                    new VmInstanceStateChange( sc.getInstanceId(), 
                                               sc.getCurrentState().getName(), 
                                               sc.getPreviousState().getName() );
                vmStateChanges.add( stateChange );
            }

        } catch (AmazonServiceException ex) {
            System.err.println( ex );
        } finally {
            return vmStateChanges;
        }
    }


    /* NOT TESTED */
    public ArrayList<VmInstanceStateChange> terminateInstances( ArrayList<String> instanceIds ) {
        TerminateInstancesRequest request = new TerminateInstancesRequest( instanceIds );
        ArrayList<VmInstanceStateChange> vmStateChanges = new ArrayList<VmInstanceStateChange>();

        // Terminate the started instance
        try {
            TerminateInstancesResult result = ec2.terminateInstances( request );
            java.util.List<InstanceStateChange> stateChanges = result.getTerminatingInstances();

            for (InstanceStateChange sc : stateChanges) {
                VmInstanceStateChange stateChange =
                    new VmInstanceStateChange( sc.getInstanceId(), 
                                               sc.getCurrentState().getName(), 
                                               sc.getPreviousState().getName() );
                vmStateChanges.add( stateChange );
            }

        } catch (AmazonServiceException ex) {
            System.err.println( ex );
        } finally {
            return vmStateChanges;
        }
    }


    public static void main( String args[] ) {
        AmazonEC2Api ec2api = new AmazonEC2Api( "AwsCredentials.properties", "quick-start-1" );

        // test getAllInstances()
        System.out.println( "########## Test getAllInstances()" );
        ArrayList<VmInstance> instances1 = ec2api.getAllInstances();
        for (VmInstance instance : instances1) {
            AmazonEC2VmInstance ec2Instance = (AmazonEC2VmInstance)instance;
            System.out.println( ec2Instance );
        }
        System.out.println();

        ArrayList<String> instanceIds = new ArrayList<String>();
        instanceIds.add( "i-f091c899" );

        // test getInstances()
        System.out.println( "########## Test getInstances()" );
        ArrayList<VmInstance> instances2 = ec2api.getInstances( instanceIds );
        for (VmInstance instance : instances2) {
            AmazonEC2VmInstance ec2Instance = (AmazonEC2VmInstance)instance;
            System.out.println( ec2Instance );
        }
        System.out.println();

        boolean testStart = false;
        if (testStart) {
            // test startInstances()
            System.out.println( "########## Test startInstances()" );
            ArrayList<VmInstanceStateChange> stateChanges1 = ec2api.startInstances( instanceIds );
            for (VmInstanceStateChange stateChange : stateChanges1)
                System.out.println( stateChange );
            System.out.println();
        } else {
            // test stopInstances()
            System.out.println( "########## Test stopInstances()" );
            ArrayList<VmInstanceStateChange> stateChanges2 = ec2api.stopInstances( instanceIds );
            for (VmInstanceStateChange stateChange : stateChanges2)
                System.out.println( stateChange );
            System.out.println();
        }
    }
}
