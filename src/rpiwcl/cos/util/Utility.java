package rpiwcl.cos.util;

import java.lang.management.*;
import java.io.InputStream;
import rpiwcl.cos.common.Constants;

public class Utility
{
    private final static OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
    private final static int cpuCount = os.getAvailableProcessors();

    public static void debugPrint(String msg){
        if( System.getProperty( "debug" ) != null )
            System.out.println(msg);
    }

    public static double getSystemLoadAverage(){
        return os.getSystemLoadAverage();    
    }

    public static double getWeightedSystemLoadAverage(){
       return os.getSystemLoadAverage() / cpuCount; 
    }

    public static Process runtimeExec( String cmd ) {
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec( new String[] {"/bin/bash", "-c", cmd} );
        } catch (Exception ex) {
            System.err.println( ex );
        }

        return proc;
    }    

    public static String runtimeExecWithStdout( String cmd ) {
        String str = null;
        try {
            Process p = runtimeExec( cmd );
            p.waitFor();

            InputStream is = p.getInputStream();
            int len = is.available();
            byte[] output = new byte[len];
            is.read( output );
            str = new String( output, "UTF-8" );
        } catch (Exception ex) {
            System.err.println( ex );
        }

        return str;
    }
}
