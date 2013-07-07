package rpiwcl.cos.runtime;

import rpiwcl.cos.runtime.Terminal;

public interface Terminal {

    public void open( String profile, String title, String user, 
                      String ipAddr, String cmd );
}
    
