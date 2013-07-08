package rpiwcl.cos.cosmanager;

import java.io.IOException;
import java.util.*;
import org.ho.yaml.Yaml;
import rpiwcl.cos.cloud.*;
import rpiwcl.cos.common.*;
import rpiwcl.cos.util.*;
import rpiwcl.cos.runtime.Terminal;


public class EntityStarter extends Controller {
    private HashMap config;
    private ArrayList cpuDb;
    private HashMap<String, CommChannel> channels;
    private Terminal terminal ;

    private static final int MAX_CONNECT_RETRY_NUM = 5;
    private static final int CONNECT_RETRY_INTERVAL = 3000; // [ms]


    public EntityStarter( int port, HashMap config, ArrayList cpuDb ) {
        super( port );

        this.config = config;
        this.cpuDb = cpuDb;
        this.channels = new HashMap<String, CommChannel>();

        // create a Terminal instance
        Class<?> clazz;
        HashMap commonConf = (HashMap)config.get( "common" );
        try {
            clazz = Class.forName((String)commonConf.get( "terminal" ));
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

    // public void hookShutdown() {
    //     Runtime.getRuntime().addShutdownHook( new Thread() {
    //             @Override
    //             public void run() {
    //                 System.out.println( "######Shutdown hook ran!" );
    //             }
    //         });
    // }
        
    public void startEntity( String id ) throws IOException {
        HashMap launchSpec = (HashMap)((HashMap)config.get( id )).get( "launch_spec" );
        HashMap runtimeConf = (HashMap)((HashMap)config.get( id )).get( "runtime_conf" );

        String ipAddr = (String)launchSpec.get( "ipaddr" );
        Integer listenPort = (Integer)launchSpec.get( "port" );
        String user = (String)launchSpec.get( "user" );
        String profile = (String)launchSpec.get( "terminal_profile" );
        String classpath = (String)launchSpec.get( "classpath" );
        String mainClass = (String)launchSpec.get( "main_class" );

        String parent = (String)launchSpec.get( "parent" );
        String cmd = null;
        if (parent == null) {
            cmd = "java -cp " + classpath + " " + mainClass + " " + id + " " + listenPort;
        }
        else { 
            HashMap parentConf = (HashMap)config.get( parent );
            HashMap parentSpec = (HashMap)parentConf.get( "launch_spec" );
            String parentIpAddr = (String)parentSpec.get( "ipaddr" );
            Integer parentPort = (Integer)parentSpec.get( "port" );
            cmd = "java -cp " + classpath + " " + mainClass + " " + 
                id + " " + listenPort + " " + parentIpAddr + " " + parentPort;
        }

        String title = "[" + id + "] " + mainClass;

        terminal.open( profile, title, user, ipAddr, cmd );

        int retry = 0;
        boolean result = false;
        CommChannel comm = null;
        while (!result && (retry < MAX_CONNECT_RETRY_NUM)) {
            try {
                comm = new CommChannel( ipAddr, listenPort );
                result = true;
            } catch (IOException ioe) {
                System.err.println( "[EntityStarter] " + ipAddr + ":" + listenPort + " not ready yet, retry=" + retry++ );
            }
            if (!result) {
                try {
                    Thread.sleep( CONNECT_RETRY_INTERVAL );
                } catch (InterruptedException ie) {
                    System.err.println( ie );
                }
            }
        }
        if (!result)
            throw new IOException();

        ConnectionHandler starterHandler = new ConnectionHandler( comm, mailbox );
        new Thread( starterHandler, "Entity connection" ).start();

        System.out.println( "[EntityStarter] Connection established on " + comm );

        channels.put( id, comm );
        msgFactory = new MessageFactory( "entity-starter", comm );
        Message msg = msgFactory.notifyConfig( Yaml.dump( runtimeConf ) );
        comm.write( msg );
    }

    
    public void handleMessage(Message msg) {
        System.out.println( "[EntityStarter] Rcved " + msg.getMethod() +
                            " from " + msg.getParam( "type" ) );

        switch( msg.getMethod() ) {
        case "start_entity":
            handleStartEntity( msg );
            break;
        case "request_cpu_db":
            handleRequestCpuDb( msg );
            break;
        }
    }

    private void handleStartEntity( Message msg ) {
        String id = (String)msg.getParam( "id" );

        System.out.println( "[EntityStarter] handleStartEntity, id=" + id );
        try {
            startEntity( id );
        } catch (IOException ioe) {
            System.err.println( "[EntityStarter] ERROR starting " + id + " failed" );
        }
    }

    private void handleRequestCpuDb( Message msg ) {
        Message resp = msgFactory.requestCpuDbResp( Yaml.dump( cpuDb ) );
        msg.getReply().write( resp );
    }


    public static void main(String[] args) {
        YamlParser yamlParser = new YamlParser();
        
        String configFile = Constants.DEFAULT_CONFIG_FILE;
        String cpuDbFile = Constants.DEFAULT_CPU_DB_FILE;
        if (2 <= args.length) {
            configFile = args[0];
            cpuDbFile = args[1];
        }

        HashMap config = (HashMap)yamlParser.parse( configFile );
        ArrayList cpuDb = (ArrayList)yamlParser.parse( cpuDbFile );
        Integer port = (Integer)config.get( "launcher_port" );

        EntityStarter runner = new EntityStarter( port, config, cpuDb );
        try {
            runner.startEntity( "cos" );
        } catch (IOException ioe) {
            System.err.println( "[EntityStarter] ERROR starting cos failed" );
            System.exit( 1 );
        }

        runner.checkMessages();
    }
}
