package rpiwcl.cos.runtime;

public interface AppInterface {
    public String open( String cosIpAddr, int cosPort, String appName );  // returns appId
    public void registerWorker( String appId, String workerRef );
    public void reportNumTasks( String appId, int numTasks );
    public void reportProgress( String appId, int completedTasks );
    public void close( String appId );
}
