package rpiwcl.cos.runtime;

public interface AppRuntime {
    public void createRuntime();
    public void notifyRuntimeCreated();
    public void migrateWorker();
}
    
