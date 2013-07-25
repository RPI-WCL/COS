package rpiwcl.cos.runtime;

import java.util.*;
import rpiwcl.cos.runtime.*;

public class SalsaRuntime implements AppRuntime {
    private static HashSet<Integer> portTable = null;
    private static int basePort = -1;
    private static int PORT_RANGE = 1000; // {basePort, ... basePort+PORT_RANGE-1} are available

    public SalsaRuntime() {
        portTable = new HashSet<Integer>();
    }

    public RuntimeInfo createRuntime( HashMap conf ) {
        String ipAddr = (String)conf.get( "ipaddr" );
        String user = (String)conf.get( "user" );
        String profile = (String)conf.get( "terminal_profile" );
        String classpath = (String)conf.get( "classpath" );
        String mainClass = (String)conf.get( "main_class" );
        String parent = (String)conf.get( "parent" );
        String appOption = (String)conf.get( "app_option" );
        String sshOption = (String)conf.get( "ssh_option" );

        if (basePort < 0)
            basePort = ((Integer)conf.get( "base_port" )).intValue();
        int port = findAvailablePort();
        if (port < 0) {
            System.err.println( "[SalsaRuntime] ERROR no valid port number found" );
            return null;
        }

        if ((appOption != null) && appOption.contains( "extip" ))
            appOption = appOption.concat( "=" + ipAddr );

        String cmd = null;
        if (appOption != null)
            cmd = "java " + appOption + " -cp " + classpath + " " + mainClass + " " + port;
        else 
            cmd = "java " + " -cp " + classpath + " " + mainClass + " " + port;
        String id = ipAddr + ":" + port;
        String title = "[" + id + "] " + mainClass;

        RuntimeInfo runtime = new RuntimeInfo( id, profile, title, user, ipAddr, 
                                               cmd, sshOption, parent );

        return runtime;
    }


    public RuntimeInfo createRuntime( HashMap conf, int port ) {
        String ipAddr = (String)conf.get( "ipaddr" );
        String user = (String)conf.get( "user" );
        String profile = (String)conf.get( "terminal_profile" );
        String classpath = (String)conf.get( "classpath" );
        String mainClass = (String)conf.get( "main_class" );
        String parent = (String)conf.get( "parent" );
        String appOption = (String)conf.get( "app_option" );
        String sshOption = (String)conf.get( "ssh_option" );

        if (port < 0) {
            System.err.println( "[SalsaRuntime] ERROR no valid port number found" );
            return null;
        }

        if ((appOption != null) && appOption.contains( "extip" ))
            appOption = appOption.concat( "=" + ipAddr );

        String cmd = null;
        if (appOption != null)
            cmd = "java " + appOption + " -cp " + classpath + " " + mainClass + " " + port;
        else 
            cmd = "java " + " -cp " + classpath + " " + mainClass + " " + port;
        String id = ipAddr + ":" + port;
        String title = "[" + id + "] " + mainClass;

        RuntimeInfo runtime = new RuntimeInfo( id, profile, title, user, ipAddr, 
                                               cmd, sshOption, parent );

        return runtime;
    }


    private static int findAvailablePort() {
        boolean found = false;

        int port;
        for (port = basePort; port < port+PORT_RANGE; port++) {
            Integer p = new Integer( port );
            if (portTable.contains( p ))
                continue;
            else {
                portTable.add( p );
                found = true;
                break;
            }
        }

        if (!found)
            port = -1;

        return port;
    }
    
    public void destroyRuntime( String id ) {
        //TODO: 1) extract a port from id, 2) remove it from the portTable
    }

    public void notifyRuntimeCreated( String runtimeId ) {
    }


    public void migrateWorker() {

    }
}
    
