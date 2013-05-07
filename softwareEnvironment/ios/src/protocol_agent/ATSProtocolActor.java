package src.protocol_agent;

// Import declarations generated by the SALSA compiler, do not modify.
import java.io.IOException;
import java.util.Vector;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

import salsa.language.Actor;
import salsa.language.ActorReference;
import salsa.language.Message;
import salsa.language.RunTime;
import salsa.language.ServiceFactory;
import gc.WeakReference;
import salsa.language.Token;
import salsa.language.exceptions.*;
import salsa.language.exceptions.CurrentContinuationException;

import salsa.language.UniversalActor;

import salsa.naming.UAN;
import salsa.naming.UAL;
import salsa.naming.MalformedUALException;
import salsa.naming.MalformedUANException;

import salsa.resources.SystemService;

import salsa.resources.ActorService;

// End SALSA compiler generated import delcarations.

import java.util.Vector;
import src.IOSTheater;
import src.profiling_agent.ProfilingAgent;
import src.profiling_agent.ATSProfilingAgent;
import src.resources.PeerServer;

public class ATSProtocolActor extends UniversalActor  {
	public static void main(String args[]) {
		UAN uan = null;
		UAL ual = null;
		if (System.getProperty("uan") != null) {
			uan = new UAN( System.getProperty("uan") );
			ServiceFactory.getTheater();
			RunTime.receivedUniversalActor();
		}
		if (System.getProperty("ual") != null) {
			ual = new UAL( System.getProperty("ual") );

			if (uan == null) {
				System.err.println("Actor Creation Error:");
				System.err.println("	uan: " + uan);
				System.err.println("	ual: " + ual);
				System.err.println("	Identifier: " + System.getProperty("identifier"));
				System.err.println("	Cannot specify an actor to have a ual at runtime without a uan.");
				System.err.println("	To give an actor a specific ual at runtime, use the identifier system property.");
				System.exit(0);
			}
			RunTime.receivedUniversalActor();
		}
		if (System.getProperty("identifier") != null) {
			if (ual != null) {
				System.err.println("Actor Creation Error:");
				System.err.println("	uan: " + uan);
				System.err.println("	ual: " + ual);
				System.err.println("	Identifier: " + System.getProperty("identifier"));
				System.err.println("	Cannot specify an identifier and a ual with system properties when creating an actor.");
				System.exit(0);
			}
			ual = new UAL( ServiceFactory.getTheater().getLocation() + System.getProperty("identifier"));
		}
		RunTime.receivedMessage();
		ATSProtocolActor instance = (ATSProtocolActor)new ATSProtocolActor(uan, ual,null).construct();
		gc.WeakReference instanceRef=new gc.WeakReference(uan,ual);
		{
			Object[] _arguments = { args };

			//preAct() for local actor creation
			//act() for remote actor creation
			if (ual != null && !ual.getLocation().equals(ServiceFactory.getTheater().getLocation())) {instance.send( new Message(instanceRef, instanceRef, "act", _arguments, false) );}
			else {instance.send( new Message(instanceRef, instanceRef, "preAct", _arguments, false) );}
		}
		RunTime.finishedProcessingMessage();
	}

	public static ActorReference getReferenceByName(UAN uan)	{ return new ATSProtocolActor(false, uan); }
	public static ActorReference getReferenceByName(String uan)	{ return ATSProtocolActor.getReferenceByName(new UAN(uan)); }
	public static ActorReference getReferenceByLocation(UAL ual)	{ return new ATSProtocolActor(false, ual); }

	public static ActorReference getReferenceByLocation(String ual)	{ return ATSProtocolActor.getReferenceByLocation(new UAL(ual)); }
	public ATSProtocolActor(boolean o, UAN __uan)	{ super(false,__uan); }
	public ATSProtocolActor(boolean o, UAL __ual)	{ super(false,__ual); }
	public ATSProtocolActor(UAN __uan,UniversalActor.State sourceActor)	{ this(__uan, null, sourceActor); }
	public ATSProtocolActor(UAL __ual,UniversalActor.State sourceActor)	{ this(null, __ual, sourceActor); }
	public ATSProtocolActor(UniversalActor.State sourceActor)		{ this(null, null, sourceActor);  }
	public ATSProtocolActor()		{  }
	public ATSProtocolActor(UAN __uan, UAL __ual, Object obj) {
		//decide the type of sourceActor
		//if obj is null, the actor must be the startup actor.
		//if obj is an actorReference, this actor is created by a remote actor

		if (obj instanceof UniversalActor.State || obj==null) {
			  UniversalActor.State sourceActor;
			  if (obj!=null) { sourceActor=(UniversalActor.State) obj;}
			  else {sourceActor=null;}

			  //remote creation message sent to a remote system service.
			  if (__ual != null && !__ual.getLocation().equals(ServiceFactory.getTheater().getLocation())) {
			    WeakReference sourceRef;
			    if (sourceActor!=null && sourceActor.getUAL() != null) {sourceRef = new WeakReference(sourceActor.getUAN(),sourceActor.getUAL());}
			    else {sourceRef = null;}
			    if (sourceActor != null) {
			      if (__uan != null) {sourceActor.getActorMemory().getForwardList().putReference(__uan);}
			      else if (__ual!=null) {sourceActor.getActorMemory().getForwardList().putReference(__ual);}

			      //update the source of this actor reference
			      setSource(sourceActor.getUAN(), sourceActor.getUAL());
			      activateGC();
			    }
			    createRemotely(__uan, __ual, "src.protocol_agent.ATSProtocolActor", sourceRef);
			  }

			  // local creation
			  else {
			    State state = new State(__uan, __ual);

			    //assume the reference is weak
			    muteGC();

			    //the source actor is  the startup actor
			    if (sourceActor == null) {
			      state.getActorMemory().getInverseList().putInverseReference("rmsp://me");
			    }

			    //the souce actor is a normal actor
			    else if (sourceActor instanceof UniversalActor.State) {

			      // this reference is part of garbage collection
			      activateGC();

			      //update the source of this actor reference
			      setSource(sourceActor.getUAN(), sourceActor.getUAL());

			      /* Garbage collection registration:
			       * register 'this reference' in sourceActor's forward list @
			       * register 'this reference' in the forward acquaintance's inverse list
			       */
			      String inverseRefString=null;
			      if (sourceActor.getUAN()!=null) {inverseRefString=sourceActor.getUAN().toString();}
			      else if (sourceActor.getUAL()!=null) {inverseRefString=sourceActor.getUAL().toString();}
			      if (__uan != null) {sourceActor.getActorMemory().getForwardList().putReference(__uan);}
			      else if (__ual != null) {sourceActor.getActorMemory().getForwardList().putReference(__ual);}
			      else {sourceActor.getActorMemory().getForwardList().putReference(state.getUAL());}

			      //put the inverse reference information in the actormemory
			      if (inverseRefString!=null) state.getActorMemory().getInverseList().putInverseReference(inverseRefString);
			    }
			    state.updateSelf(this);
			    ServiceFactory.getNaming().setEntry(state.getUAN(), state.getUAL(), state);
			    if (getUAN() != null) ServiceFactory.getNaming().update(state.getUAN(), state.getUAL());
			  }
		}

		//creation invoked by a remote message
		else if (obj instanceof ActorReference) {
			  ActorReference sourceRef= (ActorReference) obj;
			  State state = new State(__uan, __ual);
			  muteGC();
			  state.getActorMemory().getInverseList().putInverseReference("rmsp://me");
			  if (sourceRef.getUAN() != null) {state.getActorMemory().getInverseList().putInverseReference(sourceRef.getUAN());}
			  else if (sourceRef.getUAL() != null) {state.getActorMemory().getInverseList().putInverseReference(sourceRef.getUAL());}
			  state.updateSelf(this);
			  ServiceFactory.getNaming().setEntry(state.getUAN(), state.getUAL(),state);
			  if (getUAN() != null) ServiceFactory.getNaming().update(state.getUAN(), state.getUAL());
		}
	}

	public UniversalActor construct () {
		Object[] __arguments = {  };
		this.send( new Message(this, this, "construct", __arguments, null, null) );
		return this;
	}

	public class State extends UniversalActor .State {
		public ATSProtocolActor self;
		public void updateSelf(ActorReference actorReference) {
			((ATSProtocolActor)actorReference).setUAL(getUAL());
			((ATSProtocolActor)actorReference).setUAN(getUAN());
			self = new ATSProtocolActor(false,getUAL());
			self.setUAN(getUAN());
			self.setUAL(getUAL());
			self.activateGC();
		}

		public State() {
			this(null, null);
		}

		public State(UAN __uan, UAL __ual) {
			super(__uan, __ual);
			addClassName( "src.protocol_agent.ATSProtocolActor$State" );
			addMethodsForClasses();
		}

		public void process(Message message) {
			Method[] matches = getMatches(message.getMethodName());
			Object returnValue = null;
			Exception exception = null;

			if (matches != null) {
				if (!message.getMethodName().equals("die")) {activateArgsGC(message);}
				for (int i = 0; i < matches.length; i++) {
					try {
						if (matches[i].getParameterTypes().length != message.getArguments().length) continue;
						returnValue = matches[i].invoke(this, message.getArguments());
					} catch (Exception e) {
						if (e.getCause() instanceof CurrentContinuationException) {
							sendGeneratedMessages();
							return;
						} else if (e instanceof InvocationTargetException) {
							sendGeneratedMessages();
							exception = (Exception)e.getCause();
							break;
						} else {
							continue;
						}
					}
					sendGeneratedMessages();
					currentMessage.resolveContinuations(returnValue);
					return;
				}
			}

			System.err.println("Message processing exception:");
			if (message.getSource() != null) {
				System.err.println("\tSent by: " + message.getSource().toString());
			} else System.err.println("\tSent by: unknown");
			System.err.println("\tReceived by actor: " + toString());
			System.err.println("\tMessage: " + message.toString());
			if (exception == null) {
				if (matches == null) {
					System.err.println("\tNo methods with the same name found.");
					return;
				}
				System.err.println("\tDid not match any of the following: ");
				for (int i = 0; i < matches.length; i++) {
					System.err.print("\t\tMethod: " + matches[i].getName() + "( ");
					Class[] parTypes = matches[i].getParameterTypes();
					for (int j = 0; j < parTypes.length; j++) {
						System.err.print(parTypes[j].getName() + " ");
					}
					System.err.println(")");
				}
			} else {
				System.err.println("\tThrew exception: " + exception);
				exception.printStackTrace();
			}
		}

		ATSProfilingAgent profilingAgent = null;
		ActorReference decisionActor = null;
		boolean connected = true;
		Vector neighbors = new Vector();
		Vector peerServers = new Vector();
		int maxTTL = 2;
		int maxPeers = 10;
		void construct(){
			profilingAgent = (ATSProfilingAgent)IOSTheater.getProfilingAgent();
			decisionActor = IOSTheater.getDecisionActor();
			String connection = System.getProperty("connection");
			if (connection!=null) {{
				try {
					UAL ual = new UAL(connection);
					if (ual.getIdentifier().equals("/io/peerServer")) {{
						{
							// joinComputation((PeerServer)PeerServer.getReferenceByLocation(ual))
							{
								Object _arguments[] = { (PeerServer)PeerServer.getReferenceByLocation(ual) };
								Message message = new Message( self, self, "joinComputation", _arguments, null, null );
								__messages.add( message );
							}
						}
					}
}					else {{
						{
							// joinComputation((ATSProtocolActor)ATSProtocolActor.getReferenceByLocation(ual))
							{
								Object _arguments[] = { (ATSProtocolActor)ATSProtocolActor.getReferenceByLocation(ual) };
								Message message = new Message( self, self, "joinComputation", _arguments, null, null );
								__messages.add( message );
							}
						}
					}
}				}
				catch (MalformedUALException e) {
					System.err.println("Improperly specified system property: "+connection);
					System.err.println("Malformed UAL Exception: "+e);
				}

			}
}		}
		boolean waiting = false;
		public void initiateSteal() {
			if (waiting==true) {			return;
}			waiting = true;
			{
				// propagateSteal(this, new Double(profilingAgent.getAvailableProcessing()), profilingAgent.getActors(), new Integer(maxTTL))
				{
					Object _arguments[] = { this, new Double(profilingAgent.getAvailableProcessing()), profilingAgent.getActors(), new Integer(maxTTL) };
					Message message = new Message( self, self, "propagateSteal", _arguments, null, null );
					__messages.add( message );
				}
			}
		}
		public void propagateSteal(ATSProtocolActor source, double processing, Vector actorsAtSource, int timeToLive) {
			if (neighbors.contains(source)&&neighbors.size()==1) {{
				{
					// source<-stealFinished()
					{
						Object _arguments[] = {  };
						Message message = new Message( self, source, "stealFinished", _arguments, null, null );
						Object[] _propertyInfo = {  };
						message.setProperty( "priority", _propertyInfo );
						__messages.add( message );
					}
				}
				return;
			}
}			timeToLive--;
			if (timeToLive>maxTTL) {timeToLive = maxTTL-1;
}			if (neighbors.size()==0) {{
				{
					// source<-stealFinished()
					{
						Object _arguments[] = {  };
						Message message = new Message( self, source, "stealFinished", _arguments, null, null );
						Object[] _propertyInfo = {  };
						message.setProperty( "priority", _propertyInfo );
						__messages.add( message );
					}
				}
				return;
			}
}			if (timeToLive<0) {{
				{
					// source<-stealFinished()
					{
						Object _arguments[] = {  };
						Message message = new Message( self, source, "stealFinished", _arguments, null, null );
						Object[] _propertyInfo = {  };
						message.setProperty( "priority", _propertyInfo );
						__messages.add( message );
					}
				}
				return;
			}
}			ActorReference target = (ActorReference)neighbors.get((int)((double)java.lang.Math.random()*(double)neighbors.size()));
			while (target.equals(source)) {
				target = (ActorReference)neighbors.get((int)((double)java.lang.Math.random()*(double)neighbors.size()));
			}
			{
				// target<-steal(source, new Double(processing), actorsAtSource, new Integer(timeToLive))
				{
					Object _arguments[] = { source, new Double(processing), actorsAtSource, new Integer(timeToLive) };
					Message message = new Message( self, target, "steal", _arguments, null, null );
					Object[] _propertyInfo = {  };
					message.setProperty( "priority", _propertyInfo );
					__messages.add( message );
				}
			}
		}
		public void steal(ATSProtocolActor source, double processing, Vector actorsAtSource, int timeToLive) {
			if (!neighbors.contains(source)) {neighbors.add(source);
}			Token candidate = new Token("candidate");
			{
				// token candidate = decisionActor<-getBestCandidate(source.getUAL(), new Double(processing), actorsAtSource)
				{
					Object _arguments[] = { source.getUAL(), new Double(processing), actorsAtSource };
					Message message = new Message( self, decisionActor, "getBestCandidate", _arguments, null, candidate );
					__messages.add( message );
				}
			}
			{
				// resolveSteal(candidate, source, new Double(processing), actorsAtSource, new Integer(timeToLive))
				{
					Object _arguments[] = { candidate, source, new Double(processing), actorsAtSource, new Integer(timeToLive) };
					Message message = new Message( self, self, "resolveSteal", _arguments, null, null );
					__messages.add( message );
				}
			}
		}
		public void resolveSteal(ActorReference candidate, ATSProtocolActor source, double processing, Vector actorsAtSource, int timeToLive) {
			if (candidate==null) {{
				{
					// propagateSteal(source, new Double(processing), actorsAtSource, new Integer(timeToLive))
					{
						Object _arguments[] = { source, new Double(processing), actorsAtSource, new Integer(timeToLive) };
						Message message = new Message( self, self, "propagateSteal", _arguments, null, null );
						__messages.add( message );
					}
				}
			}
}			else {{
				{
					Token token_3_0 = new Token();
					// candidate<-migrate(new UAL(source.getUAL().getLocation()+candidate.getUAN().getIdentifier()))
					{
						Object _arguments[] = { new UAL(source.getUAL().getLocation()+candidate.getUAN().getIdentifier()) };
						Message message = new Message( self, candidate, "migrate", _arguments, null, token_3_0 );
						Object[] _propertyInfo = {  };
						message.setProperty( "priority", _propertyInfo );
						__messages.add( message );
					}
					// source<-stealFinished()
					{
						Object _arguments[] = {  };
						Message message = new Message( self, source, "stealFinished", _arguments, token_3_0, null );
						Object[] _propertyInfo = {  };
						message.setProperty( "priority", _propertyInfo );
						__messages.add( message );
					}
				}
			}
}		}
		public void stealFinished() {
			waiting = false;
			profilingAgent.reset();
		}
		public void joinComputation(PeerServer peerServer) {
			profilingAgent.setComputing(true);
			peerServers.add(peerServer);
			{
				// peerServer<-register(this)
				{
					Object _arguments[] = { this };
					Message message = new Message( self, peerServer, "register", _arguments, null, null );
					__messages.add( message );
				}
			}
			{
				Token token_2_0 = new Token();
				// peerServer<-getPeers(new Integer(maxPeers))
				{
					Object _arguments[] = { new Integer(maxPeers) };
					Message message = new Message( self, peerServer, "getPeers", _arguments, null, token_2_0 );
					__messages.add( message );
				}
				// addNeighbors(token)
				{
					Object _arguments[] = { token_2_0 };
					Message message = new Message( self, self, "addNeighbors", _arguments, token_2_0, null );
					__messages.add( message );
				}
			}
		}
		public void addPeer(ActorReference peer) {
			neighbors.add(peer);
		}
		public void addNeighbors(Vector neighbors) {
			if (neighbors==null) {			return;
}			for (int i = 0; i<neighbors.size(); i++){
				if (!this.equals((ActorReference)neighbors.get(i))&&!this.neighbors.contains((ActorReference)neighbors.get(i))) {{
					this.neighbors.add(neighbors.get(i));
				}
}			}
		}
		public void joinComputation(ATSProtocolActor neighbor) {
			profilingAgent.setComputing(true);
			neighbors.add(neighbor);
		}
		Vector unmigratedActors = null;
		public void leaveComputation() {
			profilingAgent.setComputing(false);
			for (int i = 0; i<peerServers.size(); i++){
				PeerServer target = (PeerServer)peerServers.get(i);
				peerServers.remove(target);
				{
					// target<-remove(this)
					{
						Object _arguments[] = { this };
						Message message = new Message( self, target, "remove", _arguments, null, null );
						__messages.add( message );
					}
				}
			}
			unmigratedActors = profilingAgent.getActorReferences();
			if (neighbors==null||neighbors.size()==0) {{
				{
					// purge()
					{
						Object _arguments[] = {  };
						Message message = new Message( self, self, "purge", _arguments, null, null );
						__messages.add( message );
					}
				}
			}
}			else {{
				for (int i = 0; i<neighbors.size(); i++){
					ActorReference target = (ActorReference)neighbors.get(i);
					{
						// target<-leaveRequest(this)
						{
							Object _arguments[] = { this };
							Message message = new Message( self, target, "leaveRequest", _arguments, null, null );
							__messages.add( message );
						}
					}
				}
			}
}		}
		public void leaveRequest(ATSProtocolActor requester) {
			neighbors.remove(requester);
			if (profilingAgent.isComputing()) {			{
				// requester<-getActor(this)
				{
					Object _arguments[] = { this };
					Message message = new Message( self, requester, "getActor", _arguments, null, null );
					__messages.add( message );
				}
			}
}			else {			{
				// requester<-notConnected(this)
				{
					Object _arguments[] = { this };
					Message message = new Message( self, requester, "notConnected", _arguments, null, null );
					__messages.add( message );
				}
			}
}		}
		public void notConnected(ATSProtocolActor neighbor) {
			neighbors.remove(neighbor);
			if (neighbors.size()==0) {			{
				// purge()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, self, "purge", _arguments, null, null );
					__messages.add( message );
				}
			}
}		}
		public void getActor(ATSProtocolActor requester) {
			if (unmigratedActors.size()>0) {{
				ActorReference target = (ActorReference)unmigratedActors.get(0);
				unmigratedActors.remove(0);
				{
					// target<-migrate(requester.getUAL().getLocation()+target.getUAN().getIdentifier())
					{
						Object _arguments[] = { requester.getUAL().getLocation()+target.getUAN().getIdentifier() };
						Message message = new Message( self, target, "migrate", _arguments, null, null );
						Object[] _propertyInfo = {  };
						message.setProperty( "priority", _propertyInfo );
						__messages.add( message );
					}
				}
				if (unmigratedActors.size()>0) {				{
					// requester<-leaveRequest(this)
					{
						Object _arguments[] = { this };
						Message message = new Message( self, requester, "leaveRequest", _arguments, null, null );
						__messages.add( message );
					}
				}
}			}
}		}
		public void purge() {
			Vector actors = profilingAgent.getActorReferences();
			for (int i = 0; i<actors.size(); i++){
				ActorReference target = (ActorReference)actors.get(i);
				{
					// target<-destroy()
					{
						Object _arguments[] = {  };
						Message message = new Message( self, target, "destroy", _arguments, null, null );
						Object[] _propertyInfo = {  };
						message.setProperty( "priority", _propertyInfo );
						__messages.add( message );
					}
				}
			}
		}
		public String status() {
			String status = "";
			status += "\tTheater: "+getUAL().getLocation()+"\n";
			status += "\tTheater connected: "+profilingAgent.isComputing()+"\n";
			status += "\tTotal messages processed: "+profilingAgent.totalProcessed()+"\n";
			status += "\tProcessed since last reset: "+profilingAgent.processed()+"\n";
			status += "\tNumber of autonomous actors: "+profilingAgent.getActorProfiles().size();
			return status;
		}
	}
}