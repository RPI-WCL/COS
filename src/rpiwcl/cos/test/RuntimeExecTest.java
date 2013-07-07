package rpiwcl.cos.tests;

import java.util.*;

public class RuntimeExecTest {
    public static void main(String[] args) {
        // String launchTerminal = "gnome-terminal --window-with-profile=\"Blue\" --command=\"ssh imais@nimbus.cs.rpi.edu java -cp ~/Git/COS_master/softwareEnvironment/salsa/salsa1.1.5.jar wwc.messaging.Theater\"";
        String launchTerminal = "gnome-terminal --window-with-profile=\"Blue\" --command=\"ssh imais@localhost java -cp ~/Git/COS/classes rpiwcl.cos.cosmanager.CosManager 49153\"";
        System.out.println( launchTerminal );

        Process proc;
        try {
            proc = Runtime.getRuntime().exec( new String[] {"/bin/bash", "-c", launchTerminal} );
        } catch (Exception ex) {
            System.err.println( ex );
        }
    }
}
