cos
===

Cloud Operating System

Crash course to running COS:

Make sure all virtual machines are set to start a VMController on boot trying to connect to the physical machine its hosted on.

Start a COSController somewhere in the local cloud. Make sure your classpath is set to the proper folder or jar if you made one.

    java cosManager.COSController

Now start a cloud manager where cos is the ip address of where you started the COSController
    
    java cloudManager.PrivateCloudController cos

Now on each of the physical machines in the private cloud start a node controller where cloud is the ip address of where the private cloud controller was started

    java nodeManager.NodeController cloud

Finally, start a vm controller on a virtual machine where nodeManager is the ip address of the physical machine the vm is running on.
    
    java vmManager.VMController nodeManager



