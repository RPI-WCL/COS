package rpiwcl.cos.cloud.pubcloud;

public class VmInstanceStateChange {
    private String instanceId;
    private String currentState;
    private String previousState;


    public VmInstanceStateChange() {
        this.instanceId = null;
        this.currentState = null;
        this.previousState = null;
    }

    public VmInstanceStateChange( String instanceId, String currentState, String previousState ) {
        this.instanceId = instanceId;
        this.currentState = currentState;
        this.previousState = previousState;
    }

    public String getInstanceId()       { return instanceId; }
    public String getCurrentState()     { return currentState; }
    public String getPreviousState()    { return previousState; }
    
    public void setInstanceId( String instanceId ) { this.instanceId = instanceId; }
    public void setCurrentState( String currentState ) { this.currentState = currentState; }
    public void setPreviousState( String previousState ) { this.previousState = previousState; }


    public String toString() {
        String str = "";
        str += "instanceId: " + instanceId + ", ";
        str += "currentState: " + currentState + ", ";
        str += "previousState: " + previousState;
        return str;
    }

}
    

