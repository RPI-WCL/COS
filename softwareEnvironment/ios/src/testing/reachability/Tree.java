package src.testing.reachability;

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

import java.io.FileInputStream;
import java.util.Vector;
import java.util.StringTokenizer;
import src.protocol_agent.ATSProtocolActor;

public class Tree extends UniversalActor  {
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
		Tree instance = (Tree)new Tree(uan, ual,null).construct();
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

	public static ActorReference getReferenceByName(UAN uan)	{ return new Tree(false, uan); }
	public static ActorReference getReferenceByName(String uan)	{ return Tree.getReferenceByName(new UAN(uan)); }
	public static ActorReference getReferenceByLocation(UAL ual)	{ return new Tree(false, ual); }

	public static ActorReference getReferenceByLocation(String ual)	{ return Tree.getReferenceByLocation(new UAL(ual)); }
	public Tree(boolean o, UAN __uan)	{ super(false,__uan); }
	public Tree(boolean o, UAL __ual)	{ super(false,__ual); }
	public Tree(UAN __uan,UniversalActor.State sourceActor)	{ this(__uan, null, sourceActor); }
	public Tree(UAL __ual,UniversalActor.State sourceActor)	{ this(null, __ual, sourceActor); }
	public Tree(UniversalActor.State sourceActor)		{ this(null, null, sourceActor);  }
	public Tree()		{  }
	public Tree(UAN __uan, UAL __ual, Object obj) {
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
			    createRemotely(__uan, __ual, "src.testing.reachability.Tree", sourceRef);
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

	public UniversalActor construct() {
		Object[] __arguments = { };
		this.send( new Message(this, this, "construct", __arguments, null, null) );
		return this;
	}

	public class State extends UniversalActor .State {
		public Tree self;
		public void updateSelf(ActorReference actorReference) {
			((Tree)actorReference).setUAL(getUAL());
			((Tree)actorReference).setUAN(getUAN());
			self = new Tree(false,getUAL());
			self.setUAN(getUAN());
			self.setUAL(getUAL());
			self.activateGC();
		}

		public void preAct(String[] arguments) {
			getActorMemory().getInverseList().removeInverseReference("rmsp://me",1);
			{
				Object[] __args={arguments};
				self.send( new Message(self,self, "act", __args, null,null,false) );
			}
		}

		public State() {
			this(null, null);
		}

		public State(UAN __uan, UAL __ual) {
			super(__uan, __ual);
			addClassName( "src.testing.reachability.Tree$State" );
			addMethodsForClasses();
		}

		public void construct() {}

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

		int bi_connected = 0;
		Vector theaters = new Vector();
		public void act(String[] arguments) {
			try {
				System.err.println("arguments.length = "+arguments.length);
				if (arguments.length!=2) {{
					this.errorMessage();
				}
}				FileInputStream file;
				byte input[];
				try {
					file = new FileInputStream(arguments[0]);
					input = new byte[file.available()];
					file.read(input);
					StringTokenizer st = new StringTokenizer(new String(input), "\n");
					while (st.hasMoreTokens()) {
						theaters.add(st.nextToken());
					}
				}
				catch (Exception e) {
					System.err.println("Error reading naming/theater information file: "+e);
					System.exit(0);
				}

				bi_connected = new Integer(arguments[1]).intValue();
			}
			catch (Exception e) {
				System.err.println("Error parsing args: "+e);
				this.errorMessage();
			}

			Token synchToken = new Token("synchToken");
			{
				// token synchToken = standardOutput<-println("Connecting the following theaters in a "+(bi_connected+1)+"-way tree.")
				{
					Object _arguments[] = { "Connecting the following theaters in a "+(bi_connected+1)+"-way tree." };
					Message message = new Message( self, standardOutput, "println", _arguments, null, synchToken );
					__messages.add( message );
				}
			}
			for (int i = 0; i<theaters.size(); i++){
				{
					// synchToken = standardOutput<-println("\t"+(String)theaters.get(i))
					Token synchToken_next = new Token("<-_next");
					{
						Object _arguments[] = { "\t"+(String)theaters.get(i) };
						Message message = new Message( self, standardOutput, "println", _arguments, null, synchToken_next );
						Object[] _propertyInfo = { synchToken };
						message.setProperty( "waitfor", _propertyInfo );
						__messages.add( message );
					}
					synchToken = synchToken_next;
				}
			}
			{
				Token token_2_1 = new Token();
				Token token_2_2 = new Token();
				// synchToken = standardOutput<-println()
				Token synchToken_next = new Token("<-_next");
				{
					Object _arguments[] = {  };
					Message message = new Message( self, standardOutput, "println", _arguments, null, synchToken_next );
					Object[] _propertyInfo = { synchToken };
					message.setProperty( "waitfor", _propertyInfo );
					__messages.add( message );
				}
				synchToken = synchToken_next;
				// standardOutput<-println("Generating links...")
				{
					Object _arguments[] = { "Generating links..." };
					Message message = new Message( self, standardOutput, "println", _arguments, synchToken_next, token_2_1 );
					__messages.add( message );
				}
				// join block
				token_2_2.setJoinDirector();
				for (int i = 0; i<theaters.size(); i++){
					String theaterName1 = (String)theaters.get(i);
					ATSProtocolActor protocolActor1 = (ATSProtocolActor)ATSProtocolActor.getReferenceByLocation(theaterName1+"io/protocolActor");
					if ((i*2)+1<theaters.size()) {{
						String theaterName2 = (String)theaters.get((i*2)+1);
						ATSProtocolActor protocolActor2 = (ATSProtocolActor)ATSProtocolActor.getReferenceByLocation(theaterName2+"io/protocolActor");
						{
							Token token_5_0 = new Token();
							// protocolActor1<-addPeer(protocolActor2)
							{
								Object _arguments[] = { protocolActor2 };
								Message message = new Message( self, protocolActor1, "addPeer", _arguments, token_2_1, token_5_0 );
								__messages.add( message );
							}
							// standardOutput<-println("\tGenerated link from "+theaterName1+" to "+theaterName2)
							{
								Object _arguments[] = { "\tGenerated link from "+theaterName1+" to "+theaterName2 };
								Message message = new Message( self, standardOutput, "println", _arguments, token_5_0, token_2_2 );
								__messages.add( message );
							}
						}
						if (bi_connected==1) {{
							{
								Token token_6_0 = new Token();
								// protocolActor2<-addPeer(protocolActor1)
								{
									Object _arguments[] = { protocolActor1 };
									Message message = new Message( self, protocolActor2, "addPeer", _arguments, token_2_1, token_6_0 );
									__messages.add( message );
								}
								// standardOutput<-println("\tGenerated link from "+theaterName2+" to "+theaterName1)
								{
									Object _arguments[] = { "\tGenerated link from "+theaterName2+" to "+theaterName1 };
									Message message = new Message( self, standardOutput, "println", _arguments, token_6_0, token_2_2 );
									__messages.add( message );
								}
							}
						}
}					}
}					if ((i*2)+2<theaters.size()) {{
						String theaterName3 = (String)theaters.get((i*2)+2);
						ATSProtocolActor protocolActor3 = (ATSProtocolActor)ATSProtocolActor.getReferenceByLocation(theaterName3+"io/protocolActor");
						{
							Token token_5_0 = new Token();
							// protocolActor1<-addPeer(protocolActor3)
							{
								Object _arguments[] = { protocolActor3 };
								Message message = new Message( self, protocolActor1, "addPeer", _arguments, token_2_1, token_5_0 );
								__messages.add( message );
							}
							// standardOutput<-println("\tGenerated link from "+theaterName1+" to "+theaterName3)
							{
								Object _arguments[] = { "\tGenerated link from "+theaterName1+" to "+theaterName3 };
								Message message = new Message( self, standardOutput, "println", _arguments, token_5_0, token_2_2 );
								__messages.add( message );
							}
						}
						if (bi_connected==1) {{
							{
								Token token_6_0 = new Token();
								// protocolActor3<-addPeer(protocolActor1)
								{
									Object _arguments[] = { protocolActor1 };
									Message message = new Message( self, protocolActor3, "addPeer", _arguments, token_2_1, token_6_0 );
									__messages.add( message );
								}
								// standardOutput<-println("\tGenerated link from theater "+theaterName3+" to theater "+theaterName1)
								{
									Object _arguments[] = { "\tGenerated link from theater "+theaterName3+" to theater "+theaterName1 };
									Message message = new Message( self, standardOutput, "println", _arguments, token_6_0, token_2_2 );
									__messages.add( message );
								}
							}
						}
}					}
}				}
				addJoinToken(token_2_2);
				// standardOutput<-println("finished.")
				{
					Object _arguments[] = { "finished." };
					Message message = new Message( self, standardOutput, "println", _arguments, token_2_2, null );
					__messages.add( message );
				}
			}
		}
		public void errorMessage() {
			System.err.println("Usage: ");
			System.err.println("\tjava io.testing.reachability.Tree <theater information file> <0 for one way, 1 for two way>");
			System.exit(0);
		}
	}
}