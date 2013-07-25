package rpiwcl.cos.runtime;

import java.util.HashMap;
import rpiwcl.cos.runtime.RuntimeInfo;

public interface AppRuntime {
    public RuntimeInfo createRuntime( HashMap conf );
    public RuntimeInfo createRuntime( HashMap conf, int port ); // kludge for now
    public void notifyRuntimeCreated( String runtimeId );
    public void migrateWorker();
}
    
