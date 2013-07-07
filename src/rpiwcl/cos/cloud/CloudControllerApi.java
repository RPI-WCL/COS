package rpiwcl.cos.cloud;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import rpiwcl.cos.common.*;
import rpiwcl.cos.util.*;
import rpiwcl.cos.runtime.Terminal;

public abstract class CloudControllerApi {

    protected CommChannel cloudComm = null;
    protected Map cosConf = null;
    protected Map cloudConf = null;
    protected List cpuDb = null;
    protected Terminal terminal = null;

    public CloudControllerApi( LinkedBlockingQueue<Message> mailbox, 
                               Map cosConf, Map cloudConf, List cpuDb ) {

        this.cosConf = cosConf;
        this.cloudConf = cloudConf;
        this.cpuDb = cpuDb;

        // create a Terminal instance
        Class<?> clazz;
        try {
            clazz = Class.forName((String)cosConf.get( "terminal" ));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException( e );
        }
        try {
            this.terminal = (Terminal)clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException( e );
        } catch (IllegalAccessException e) {
            throw new RuntimeException( e );
        }
    }


    public void open() {
        String cosIpAddr = (String)cosConf.get("cos_ipaddr");
        Integer cosPort = (Integer)cosConf.get("cos_port");

        String profile = (String)cloudConf.get("cloud_terminal_profile");
        String user = (String)cloudConf.get("cloud_user");
        String ipAddr = (String)cloudConf.get("cloud_ipaddr");
        Integer port = (Integer)cloudConf.get("cloud_port");
        String classpath = (String)cloudConf.get("cloud_classpath");
        String cmd = "java -cp " + classpath + " rpiwcl.cos.cloud.CloudController " + 
            cosIpAddr + " " + cosPort + " " + port;

        if (ipAddr.equals( "localhost" ) || ipAddr.equals( "127.0.0.1" )) {
            terminal.openLocal( profile, null, cmd );
        }
        else if ((ipAddr != null) && (user != null)) {
            terminal.openRemote( profile, null, user, ipAddr, cmd );
        }
    }


    public void connect( String ipAddr, int port ) {
        // cloudComm = new CommChannel( ipAddr, port );
        // ConnectionHandler cloudHandler = new ConnectionHandler( cloudComm, mailbox );
        // new Thread( cloudHandler, "Cloud connection" ).start();
    }

    abstract protected void createRuntimes( int numRuntimes );
}
