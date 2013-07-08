package rpiwcl.cos.runtime;

public interface AppInterface {
    public int open(String appName, int numTasks);
    public void close(int id);
    public void registerWorker(int id, String workerRef);
    public void reportProgress(int id, int completedTasks);
}

