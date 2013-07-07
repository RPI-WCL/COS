package rpiwcl.cos.runtime;

public class AppInterface {
    public int open(String appName, int numTasks);
    public void close(int id);
    public void registerWorker(int id, String workerRef);
    public void reportProgress(int id, int completedTasks);
}

