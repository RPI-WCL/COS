package src;

import salsa.language.ActorReference;
import salsa.language.ServiceFactory;
import wwc.messaging.Theater;
import src.profiling_agent.ProfilingAgent;

public class IOSTheater extends Theater {
	static ActorReference protocolActor = null;
	static ActorReference decisionActor = null;
	static ProfilingAgent profilingAgent = null;

	public static synchronized ActorReference getProtocolActor() {
		if (protocolActor == null) {
			//Check for the protocol actor
			//default = io.ats.ATSProtocolActor
			String className = System.getProperty( "protocol" );  
			if (className == null) className = "src.protocol_agent.FuzzyProtocolActor";
//			if (className == null) className = "src.protocol_agent.ATSProtocolActor";

			try {
				protocolActor = ServiceFactory.createActor(className, "io/protocolActor");
			} catch (Exception e) {
				System.err.println("Error creating protocol actor: " + className);
				System.err.println("\t" + e);
				e.printStackTrace();
			}
		}
		return protocolActor;
	}

	public static synchronized ActorReference getDecisionActor() {
		if (decisionActor == null) {
			//Check for the decision actor
			//default = io.ats.ATSDecisionActor
			String className = System.getProperty( "decision" );  
			if (className == null) className = "src.decision_agent.FuzzyDecisionActor";
//			if (className == null) className = "src.decision_agent.ATSDecisionActor";

			try {
				decisionActor = ServiceFactory.createActor(className, "io/decisionActor");
			} catch (Exception e) {
				System.err.println("Error creating decision actor: " + className);
				System.err.println("\t" + e);
				e.printStackTrace();
			}
		}
		return decisionActor;
	}

	public static synchronized ProfilingAgent getProfilingAgent() {
		if (profilingAgent == null) {
			//Check for the profiling agent
			//default = io.ats.ATSProfilingAgent
			String className = System.getProperty( "profiling" );  
			if (className == null) className = "src.profiling_agent.ATSProfilingAgent";

			try {
				profilingAgent = (ProfilingAgent)Class.forName( className ).newInstance();
			} catch (Exception e) {
				System.err.println("Error creating profiling agent: " + className);
				System.err.println("\t" + e);
				e.printStackTrace();
			}
		}
		return profilingAgent;
	}

	public IOSTheater() {
                Theater.main(null);

		getProfilingAgent();
		getDecisionActor();
		getProtocolActor();
	}

	public static void main(String[] arguments) {
		Theater.main(arguments);

		getProfilingAgent();
		getDecisionActor();
		getProtocolActor();
	}
}
