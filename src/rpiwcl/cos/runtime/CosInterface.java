package rpiwcl.cos.runtime;

import java.io.*;
import java.util.*;
import rpiwcl.cos.util.*;
import rpiwcl.cos.common.*;

public class CosInterface implements Serializable {
    private String appId;
    transient private CommChannel cos;
    private MessageFactory msgFactory;
    
    public CosInterface( String cosIpAddr, int cosPort, String appName ) {
        // connecting to CosManager
        try {
            cos = new CommChannel( cosIpAddr, cosPort );
        } catch (IOException ioe) {
            System.err.println( "[CosIf] this should not happen, CosManager must be running" );
        }

        appId = cos.getLocalAddr() + ":" + appName;
        msgFactory = new MessageFactory( appId );

        Utility.debugPrint( "[CosIf] constructor, appId=" + appId + ", cos=" + cos );
    }

    public void open() {
        Utility.debugPrint( "[CosIf] open, appId=" + appId + ", cos=" + cos );
        Message msg = msgFactory.cosIfOpen( appId );
        cos.write( msg );
    }

    public void reportNumTasks( int numTasks ) {
        Utility.debugPrint( "[CosIf] reportNumTasks, appId=" + appId + ", cos=" + cos );
        Message msg = msgFactory.cosIfReportNumTasks( appId, numTasks );
        cos.write( msg );
    }

    public void registerWorkers( ArrayList workerRefs ) {
        Utility.debugPrint( "[CosIf] registerWorkers, appId=" + appId + ", cos=" + cos );
        Message msg = msgFactory.cosIfRegisterWorkers( appId, workerRefs );
        cos.write( msg );
    }

    public void reportProgress( int completedTasks ) {
        Utility.debugPrint( "[CosIf] reportProgress, appId=" + appId + ", cos=" + cos );
        Message msg = msgFactory.cosIfReportProgress( appId, completedTasks );
        cos.write( msg );
    }

    public void close() {
        Utility.debugPrint( "[CosIf] close, appId=" + appId + ", cos=" + cos );
        Message msg = msgFactory.cosIfClose( appId );
        cos.write( msg );
    }
}
