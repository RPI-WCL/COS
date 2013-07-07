package src.profiling_agent;

import salsa.language.Message;
import salsa.naming.UAN;
import salsa.naming.UAL;
import java.util.Vector;


public interface ProfilingAgent {
	public Vector getActors();

	//The following methods notifiy the profiling agent of
	//actors entering and exiting the theater due to 
	//migration and binding
	public void addProfile(UAN uan);
	public void removeProfile(UAN uan);

	//The profiling agent updates its actor profiles based on
	//message sending with these methods
	public void beginSend(UAN source, UAN target);
	public void endSend(UAN source, UAN target);

	//The profiling agent updates its actor profiles based on
	//message reception with this method,  the first parameter
	//is the actor whose profile will be updated
	public void received(UAN target, UAN source);
	public void sent(UAN source, UAN target);

	//The following methods notify the profiling agent of
	//the start of a message being processed and the end
	//of a message being processed, with a UAN or UAL to
	//identify the sending actor
	public void beginProcessed(UAN uan);
	public void endProcessed(UAN uan);
}
