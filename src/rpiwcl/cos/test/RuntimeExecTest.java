package rpiwcl.cos.test;

import java.util.*;
import java.util.regex.*;
import java.io.InputStream;
import rpiwcl.cos.util.Utility;

public class RuntimeExecTest {
    void testLaunchTerminal() {
        String launchTerminal = "gnome-terminal --window-with-profile=\"Blue\" --command=\"ssh imais@localhost java -cp ~/Git/COS/classes rpiwcl.cos.cosmanager.CosManager 49153\"";
        System.out.println( launchTerminal );

        Process proc;
        try {
            proc = Runtime.getRuntime().exec( new String[] {"/bin/bash", "-c", launchTerminal} );
        } catch (Exception ex) {
            System.err.println( ex );
        }
    }        

    void testGetOutput() {
        String str = Utility.runtimeExecWithStdout( "cat /proc/cpuinfo | grep 'model name' | cut -c14- " );
        Pattern pt = Pattern.compile( "\n\\Z" );
        Matcher match = pt.matcher( str );
        System.out.println( match.replaceAll( "" ) );
    }        

    public static void main(String[] args) {
        RuntimeExecTest runtime = new RuntimeExecTest();
        // runtime.testLaunchTerminal();
        runtime.testGetOutput();
    }
}
