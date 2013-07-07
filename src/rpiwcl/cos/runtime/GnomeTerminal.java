package rpiwcl.cos.runtime;

import rpiwcl.cos.runtime.Terminal;

public class GnomeTerminal implements Terminal {

    public void open( String profile, String title, String user, 
                       String ipAddr, String cmd ) {
        // if (ipAddr.equals( "localhost" ) || ipAddr.equals( "127.0.0.1" ))
        //     openLocal( profile, title, cmd );
        // else if ((ipAddr != null) && (user != null)) {
            openRemote( profile, title, user, ipAddr, cmd );
        // }
    }

    // does not work properly
    private void openLocal( String profile, String title, String cmd ) {
        String terminalCmd = "gnome-terminal --window-with-profile=\"" + profile + 
            "\" --command=\"" + cmd + "\"";
        System.out.println( terminalCmd );
        runtimeExec( terminalCmd );
    }

    private void openRemote( String profile, String title, String user, 
                             String ipAddr, String cmd ) {
        String terminalCmd = "gnome-terminal --window-with-profile=\"" + profile + "\"" +
            " --title=\"" + title + "\"" +
            " --command=\"ssh " + user + "@" + ipAddr + " " + cmd + "\"";
        System.out.println( terminalCmd );
        runtimeExec( terminalCmd );
    }

    private void runtimeExec( String cmd ) {
        Process proc;
        try {
            proc = Runtime.getRuntime().exec( new String[] {"/bin/bash", "-c", cmd} );
        } catch (Exception ex) {
            System.err.println( ex );
        }
    }        
}
    
