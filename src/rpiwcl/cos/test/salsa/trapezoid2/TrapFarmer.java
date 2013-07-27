package rpiwcl.cos.test.salsa.trapezoid2;

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

import java.io.*;
import java.util.*;
import rpiwcl.cos.test.salsa.trapezoid2.*;

public class TrapFarmer extends UniversalActor  {
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
		TrapFarmer instance = (TrapFarmer)new TrapFarmer(uan, ual,null).construct();
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

	public static ActorReference getReferenceByName(UAN uan)	{ return new TrapFarmer(false, uan); }
	public static ActorReference getReferenceByName(String uan)	{ return TrapFarmer.getReferenceByName(new UAN(uan)); }
	public static ActorReference getReferenceByLocation(UAL ual)	{ return new TrapFarmer(false, ual); }

	public static ActorReference getReferenceByLocation(String ual)	{ return TrapFarmer.getReferenceByLocation(new UAL(ual)); }
	public TrapFarmer(boolean o, UAN __uan)	{ super(false,__uan); }
	public TrapFarmer(boolean o, UAL __ual)	{ super(false,__ual); }
	public TrapFarmer(UAN __uan,UniversalActor.State sourceActor)	{ this(__uan, null, sourceActor); }
	public TrapFarmer(UAL __ual,UniversalActor.State sourceActor)	{ this(null, __ual, sourceActor); }
	public TrapFarmer(UniversalActor.State sourceActor)		{ this(null, null, sourceActor);  }
	public TrapFarmer()		{  }
	public TrapFarmer(UAN __uan, UAL __ual, Object obj) {
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
			    createRemotely(__uan, __ual, "rpiwcl.cos.test.salsa.trapezoid2.TrapFarmer", sourceRef);
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
		public TrapFarmer self;
		public void updateSelf(ActorReference actorReference) {
			((TrapFarmer)actorReference).setUAL(getUAL());
			((TrapFarmer)actorReference).setUAN(getUAN());
			self = new TrapFarmer(false,getUAL());
			self.setUAN(getUAN());
			self.setUAL(getUAL());
			self.activateGC();
		}

		public State() {
			this(null, null);
		}

		public State(UAN __uan, UAL __ual) {
			super(__uan, __ual);
			addClassName( "rpiwcl.cos.test.salsa.trapezoid2.TrapFarmer$State" );
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

		double a = 0.0;
		double b = 1.0;
		int numWorkers = 2;
		long initialTime;
		String nameServer;
		ArrayList configs;
		int numTasks = 0;
		int REPORT_PROGRESS_INTERVAL = 10000;
		int[] completedTasks;
		String appName;
		public void submitJob(int a, int b, int numTasks, int numWorkers, String nameServer, ArrayList configs) {
			this.a = a;
			this.b = b;
			this.numTasks = numTasks;
			this.numWorkers = numWorkers;
			this.nameServer = nameServer;
			this.configs = configs;
			this.completedTasks = new int[numWorkers];
			{
				Token token_2_0 = new Token();
				Token token_2_1 = new Token();
				// distributeWork()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, self, "distributeWork", _arguments, null, token_2_0 );
					__messages.add( message );
				}
				// displayResults(token)
				{
					Object _arguments[] = { token_2_0 };
					Message message = new Message( self, self, "displayResults", _arguments, token_2_0, token_2_1 );
					__messages.add( message );
				}
				// endTimer()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, self, "endTimer", _arguments, token_2_1, currentMessage.getContinuationToken() );
					__messages.add( message );
				}
				throw new CurrentContinuationException();
			}
		}
		public Double distributeWork() {
			TrapWorker[] workers = new TrapWorker[numWorkers];
			ArrayList uans = new ArrayList();
			int id = 0;
			for (int i = 0; i<configs.size(); i++){
				String config = (String)configs.get(i);
				String[] splits = config.split(",");
				String theater = splits[1];
				System.out.println("Sending "+splits[3]+" actors to "+splits[0]+"("+splits[2]+")");
				for (int j = 0; j<Integer.parseInt(splits[3]); j++){
					String uan = "uan://"+nameServer+"/a"+id;
					String ual = "rmsp://"+theater+"/a"+id;
					System.out.println(" Sending actor "+uan+" to "+ual);
					workers[id] = ((TrapWorker)new TrapWorker(new UAN(uan), new UAL(ual),this).construct(id, this.getUAN().toString()));
					id++;
				}
			}
			System.out.println(">>>>>>Starting the computation");
			initialTime = System.currentTimeMillis();
			double h = (b-a)/numTasks;
			int local_n;
			double local_a;
			double local_b;
			int total_n = 0;
			id = 0;
			{
				Token token_2_0 = new Token();
				// join block
				token_2_0.setJoinDirector();
				for (int i = 0; i<configs.size(); i++){
					String config = (String)configs.get(i);
					String[] splits = config.split(",");
					for (int j = 4; j<splits.length; j++){
						local_n = Integer.parseInt(splits[j]);
						local_a = a+total_n*h;
						local_b = local_a+local_n*h;
						{
							// workers[id]<-trap(local_a, local_b, local_n, h)
							{
								Object _arguments[] = { local_a, local_b, local_n, h };
								Message message = new Message( self, workers[id], "trap", _arguments, null, token_2_0 );
								__messages.add( message );
							}
						}
						total_n += local_n;
						id++;
					}
				}
				addJoinToken(token_2_0);
				// addUpIntegrals(token)
				{
					Object _arguments[] = { token_2_0 };
					Message message = new Message( self, self, "addUpIntegrals", _arguments, token_2_0, currentMessage.getContinuationToken() );
					__messages.add( message );
				}
				throw new CurrentContinuationException();
			}
		}
		public void endTimer() {
			long finalTime = System.currentTimeMillis();
			long runningTime = finalTime-initialTime;
			{
				// standardOutput<-println("Running time for Trapezoidal"+" approximation is "+((double)runningTime/1000)+" sec")
				{
					Object _arguments[] = { "Running time for Trapezoidal"+" approximation is "+((double)runningTime/1000)+" sec" };
					Message message = new Message( self, standardOutput, "println", _arguments, null, null );
					__messages.add( message );
				}
			}
		}
		public double addUpIntegrals(Object[] results) {
			double total = 0.0;
			for (int i = 0; i<results.length; i++){
				total += ((Double)results[i]).doubleValue();
			}
			return total;
		}
		public void displayResults(double result) {
			{
				// standardOutput<-println("With n = "+numTasks+" trapezoids, our estimate of the integral on ("+a+","+b+")="+result)
				{
					Object _arguments[] = { "With n = "+numTasks+" trapezoids, our estimate of the integral on ("+a+","+b+")="+result };
					Message message = new Message( self, standardOutput, "println", _arguments, null, null );
					__messages.add( message );
				}
			}
		}
		long lastReportTime = 0;
		public void reportProgress(int id, int completed) {
			long currentTime = System.currentTimeMillis();
			completedTasks[id] = completed;
			if (lastReportTime+REPORT_PROGRESS_INTERVAL<=currentTime) {{
				lastReportTime = currentTime;
				int sum = 0;
				for (int i = 0; i<numWorkers; i++)sum += completedTasks[i];
				System.out.println(" "+sum+"/"+numTasks+" ("+(100*sum/numTasks)+"%) completed");
			}
}		}
	}
}