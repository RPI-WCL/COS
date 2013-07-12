package rpiwcl.cos.runtime;

public interface AppRuntime {
    public String createRuntime();
    public void notifyRuntimeCreated();
    public void migrateWorker();
}
    
