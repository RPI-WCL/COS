package rpiwcl.cos.test.salsa.stars;

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
import rpiwcl.cos.test.salsa.stars.*;

public class Stars extends UniversalActor  {
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
		Stars instance = (Stars)new Stars(uan, ual,null).construct();
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

	public static ActorReference getReferenceByName(UAN uan)	{ return new Stars(false, uan); }
	public static ActorReference getReferenceByName(String uan)	{ return Stars.getReferenceByName(new UAN(uan)); }
	public static ActorReference getReferenceByLocation(UAL ual)	{ return new Stars(false, ual); }

	public static ActorReference getReferenceByLocation(String ual)	{ return Stars.getReferenceByLocation(new UAL(ual)); }
	public Stars(boolean o, UAN __uan)	{ super(false,__uan); }
	public Stars(boolean o, UAL __ual)	{ super(false,__ual); }
	public Stars(UAN __uan,UniversalActor.State sourceActor)	{ this(__uan, null, sourceActor); }
	public Stars(UAL __ual,UniversalActor.State sourceActor)	{ this(null, __ual, sourceActor); }
	public Stars(UniversalActor.State sourceActor)		{ this(null, null, sourceActor);  }
	public Stars()		{  }
	public Stars(UAN __uan, UAL __ual, Object obj) {
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
			    createRemotely(__uan, __ual, "rpiwcl.cos.test.salsa.stars.Stars", sourceRef);
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
		public Stars self;
		public void updateSelf(ActorReference actorReference) {
			((Stars)actorReference).setUAL(getUAL());
			((Stars)actorReference).setUAN(getUAN());
			self = new Stars(false,getUAL());
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
			addClassName( "rpiwcl.cos.test.salsa.stars.Stars$State" );
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

		int NUM_WORKERS;
		int numStars;
		Point3d[] stars;
		Result result;
		Vector closestNeighbors;
		float closestDist = Float.MAX_VALUE;
		Vector farthestNeighbors;
		float farthestDist = 0.0F;
		Vector idealHubStars;
		float minMaxDist = Float.MAX_VALUE;
		Vector idealJailStars;
		float maxMinDist = 0.0F;
		Vector idealCapitalStars;
		float minAvgDist = Float.MAX_VALUE;
		float sumWorkerTime = 0.0F;
		String defaultTheatersFile = "./theaters.txt";
		String nameServer;
		Vector theaters;
		long initialTime;
		public void act(String args[]) {
			if (args.length<2) {{
				System.err.println("Error: invalid usage");
				System.err.println("Usage: args[0]=<# of workers> args[1]=<stars file> [args[2]=<theaters file>]");
				return;
			}
}			boolean useDefaultTheatersFile = (args.length==2);
			NUM_WORKERS = Integer.parseInt(args[0]);
			closestNeighbors = new Vector();
			farthestNeighbors = new Vector();
			idealHubStars = new Vector();
			idealJailStars = new Vector();
			idealCapitalStars = new Vector();
			if (useDefaultTheatersFile) {{
				{
					Token token_3_0 = new Token();
					Token token_3_1 = new Token();
					Token token_3_2 = new Token();
					Token token_3_3 = new Token();
					// readTheatersFile(defaultTheatersFile)
					{
						Object _arguments[] = { defaultTheatersFile };
						Message message = new Message( self, self, "readTheatersFile", _arguments, null, token_3_0 );
						__messages.add( message );
					}
					// readStarsFile(args[1])
					{
						Object _arguments[] = { args[1] };
						Message message = new Message( self, self, "readStarsFile", _arguments, token_3_0, token_3_1 );
						__messages.add( message );
					}
					// distributeWork()
					{
						Object _arguments[] = {  };
						Message message = new Message( self, self, "distributeWork", _arguments, token_3_1, token_3_2 );
						__messages.add( message );
					}
					// collectResults(token)
					{
						Object _arguments[] = { token_3_2 };
						Message message = new Message( self, self, "collectResults", _arguments, token_3_2, token_3_3 );
						__messages.add( message );
					}
					// displayResults()
					{
						Object _arguments[] = {  };
						Message message = new Message( self, self, "displayResults", _arguments, token_3_3, null );
						__messages.add( message );
					}
				}
			}
}			else {{
				{
					Token token_3_0 = new Token();
					Token token_3_1 = new Token();
					Token token_3_2 = new Token();
					Token token_3_3 = new Token();
					// readTheatersFile(args[2])
					{
						Object _arguments[] = { args[2] };
						Message message = new Message( self, self, "readTheatersFile", _arguments, null, token_3_0 );
						__messages.add( message );
					}
					// readStarsFile(args[1])
					{
						Object _arguments[] = { args[1] };
						Message message = new Message( self, self, "readStarsFile", _arguments, token_3_0, token_3_1 );
						__messages.add( message );
					}
					// distributeWork2()
					{
						Object _arguments[] = {  };
						Message message = new Message( self, self, "distributeWork2", _arguments, token_3_1, token_3_2 );
						__messages.add( message );
					}
					// collectResults(token)
					{
						Object _arguments[] = { token_3_2 };
						Message message = new Message( self, self, "collectResults", _arguments, token_3_2, token_3_3 );
						__messages.add( message );
					}
					// displayResults()
					{
						Object _arguments[] = {  };
						Message message = new Message( self, self, "displayResults", _arguments, token_3_3, null );
						__messages.add( message );
					}
				}
			}
}		}
		public void readTheatersFile(String theatersFile) {
			theaters = new Vector();
			String theater;
			try {
				BufferedReader in = new BufferedReader(new FileReader(theatersFile));
				nameServer = in.readLine();
				while ((theater=in.readLine())!=null) {
					theaters.add(theater);
				}
				in.close();
			}
			catch (IOException ex) {
				System.err.println("Error: Can't open the file "+theatersFile+" for reading.");
			}

		}
		public void readStarsFile(String starsFile) {
			try {
				BufferedReader in = new BufferedReader(new FileReader(starsFile));
				numStars = Integer.parseInt(in.readLine());
				stars = new Point3d[numStars];
				int line = 0, i = 0, numDuplicated = 0;
				String str;
				while ((line<numStars)&&(str=in.readLine())!=null) {
					Point3d p = new Point3d(str);
					boolean flag = false;
					for (int j = 0; j<i; j++){
						if (p.isEqual(stars[j])) {{
							flag = true;
							numDuplicated++;
break;						}
}					}
					if (!flag) {stars[i++] = p;
}					line++;
				}
				in.close();
				System.out.println("Read "+numStars+" lines from the input file");
				numStars = i;
				System.out.println(numStars+" effective data ("+numDuplicated+" duplications found)");
			}
			catch (IOException ex) {
				System.err.println("Error: Can't open the file "+starsFile+" for reading.");
			}

		}
		public Object[] getResults(Object[] objs) {
			return objs;
		}
		public Vector assignTasks(int i) {
			Vector tasks = new Vector();
			int numTasks = (numStars/2)/NUM_WORKERS;
			int currTask = i*numTasks;
			int numComp = 0;
			if (i<NUM_WORKERS-1) {{
				for (int j = currTask; j<(currTask+numTasks); j++){
					Integer task1 = new Integer(j);
					tasks.add(task1);
					numComp += numStars-task1-1;
					Integer task2 = new Integer(numStars-j-1);
					tasks.add(task2);
					numComp += numStars-task2-1;
				}
			}
}			else {{
				for (int j = currTask; j<numStars-currTask; j++){
					Integer task = new Integer(j);
					tasks.add(task);
					numComp += numStars-task-1;
				}
			}
}			System.out.println("Worker "+i+" is computing "+numComp+" distances");
			return tasks;
		}
		public Object[] distributeWork() {
			StarsWorker[] workers = new StarsWorker[NUM_WORKERS];
			{
				// standardOutput<-println(">>>>>>Starting the computation")
				{
					Object _arguments[] = { ">>>>>>Starting the computation" };
					Message message = new Message( self, standardOutput, "println", _arguments, null, null );
					__messages.add( message );
				}
			}
			initialTime = System.currentTimeMillis();
			{
				Token token_2_0 = new Token();
				// join block
				token_2_0.setJoinDirector();
				for (int i = 0; i<NUM_WORKERS; i++){
					workers[i] = ((StarsWorker)new StarsWorker(new UAN("uan://"+nameServer+"/a"+i), new UAL("rmsp://"+theaters.get(i%theaters.size())+"/a"+i),this).construct(i, stars, numStars, assignTasks(i)));
					{
						// workers[i]<-compute()
						{
							Object _arguments[] = {  };
							Message message = new Message( self, workers[i], "compute", _arguments, null, token_2_0 );
							__messages.add( message );
						}
					}
				}
				addJoinToken(token_2_0);
				// getResults(token)
				{
					Object _arguments[] = { token_2_0 };
					Message message = new Message( self, self, "getResults", _arguments, token_2_0, currentMessage.getContinuationToken() );
					__messages.add( message );
				}
				throw new CurrentContinuationException();
			}
		}
		public Object[] distributeWork2() {
			StarsWorker[] workers = new StarsWorker[NUM_WORKERS];
			{
				// standardOutput<-println(">>>>>>Starting the computation")
				{
					Object _arguments[] = { ">>>>>>Starting the computation" };
					Message message = new Message( self, standardOutput, "println", _arguments, null, null );
					__messages.add( message );
				}
			}
			initialTime = System.currentTimeMillis();
			{
				Token token_2_0 = new Token();
				// join block
				token_2_0.setJoinDirector();
				int actor = 0;
				for (int i = 0; i<theaters.size(); i++){
					String[] dest = ((String)theaters.get(i)).split(",");
					for (int j = 0; j<Integer.parseInt(dest[1]); j++){
						if (actor<NUM_WORKERS) {{
							workers[actor] = ((StarsWorker)new StarsWorker(new UAN("uan://"+nameServer+"/a"+actor), new UAL("rmsp://"+dest[0]+"/a"+actor),this).construct(actor, stars, numStars, assignTasks(actor)));
							{
								// workers[actor]<-compute()
								{
									Object _arguments[] = {  };
									Message message = new Message( self, workers[actor], "compute", _arguments, null, token_2_0 );
									__messages.add( message );
								}
							}
							actor++;
						}
}					}
				}
				addJoinToken(token_2_0);
				// getResults(token)
				{
					Object _arguments[] = { token_2_0 };
					Message message = new Message( self, self, "getResults", _arguments, token_2_0, currentMessage.getContinuationToken() );
					__messages.add( message );
				}
				throw new CurrentContinuationException();
			}
		}
		public Boolean isDifferentPair(Vector pairs1, Vector pairs2) {
			for (int i = 0; i<pairs1.size(); i++){
				Integer[] star1 = (Integer[])pairs1.elementAt(i);
				for (int j = 0; j<pairs2.size(); j++){
					Integer[] star2 = (Integer[])pairs2.elementAt(i);
					if (((star1[0].intValue()==star2[0].intValue())&&(star1[1].intValue()==star2[1].intValue()))||((star1[1].intValue()==star2[0].intValue())&&(star1[0].intValue()==star2[1].intValue()))) {					return false;
}				}
			}
			return true;
		}
		public void collectResults(Object[] objs) {
			if (objs==null) {{
				System.err.println("Error: null objects");
				return;
			}
}			result = new Result(numStars);
			for (int i = 0; i<objs.length; i++){
				Result workerResult = (Result)objs[i];
				for (int j = 0; j<numStars; j++){
					if (result.maxDist[j]<workerResult.maxDist[j]) {{
						result.maxDistStars[j].clear();
						result.maxDistStars[j].addAll(workerResult.maxDistStars[j]);
						result.maxDist[j] = workerResult.maxDist[j];
					}
}					else {if (workerResult.maxDist[j]!=0.0F&&result.maxDist[j]==workerResult.maxDist[j]) {result.maxDistStars[j].addAll(workerResult.maxDistStars[j]);
}}					if (workerResult.minDist[j]<result.minDist[j]) {{
						result.minDistStars[j].clear();
						result.minDistStars[j].addAll(workerResult.minDistStars[j]);
						result.minDist[j] = workerResult.minDist[j];
					}
}					else {if (workerResult.minDist[j]!=Float.MAX_VALUE&&result.minDist[j]==workerResult.minDist[j]) {result.minDistStars[j].addAll(workerResult.minDistStars[j]);
}}					result.sumDist[j] += workerResult.sumDist[j];
				}
				sumWorkerTime += workerResult.getElapsedTime();
			}
			for (int i = 0; i<numStars; i++){
				if (result.minDist[i]<closestDist) {{
					closestNeighbors.clear();
					closestNeighbors.addAll(result.minDistStars[i]);
					closestDist = result.minDist[i];
				}
}				else {if ((closestDist==result.minDist[i])&&isDifferentPair(result.minDistStars[i], closestNeighbors)) {closestNeighbors.addAll(result.minDistStars[i]);
}}				if (farthestDist<result.maxDist[i]) {{
					farthestNeighbors.clear();
					farthestNeighbors.addAll(result.maxDistStars[i]);
					farthestDist = result.maxDist[i];
				}
}				else {if ((farthestDist==result.maxDist[i])&&isDifferentPair(result.maxDistStars[i], farthestNeighbors)) {farthestNeighbors.addAll(result.maxDistStars[i]);
}}				if (result.maxDist[i]<minMaxDist) {{
					idealHubStars.clear();
					idealHubStars.addAll(result.maxDistStars[i]);
					minMaxDist = result.maxDist[i];
				}
}				else {if ((minMaxDist==result.maxDist[i])&&isDifferentPair(result.maxDistStars[i], idealHubStars)) {idealHubStars.addAll(result.maxDistStars[i]);
}}				if (maxMinDist<result.minDist[i]) {{
					idealJailStars.clear();
					idealJailStars.addAll(result.minDistStars[i]);
					maxMinDist = result.minDist[i];
				}
}				else {if ((maxMinDist==result.minDist[i])&&isDifferentPair(result.minDistStars[i], idealJailStars)) {idealJailStars.addAll(result.minDistStars[i]);
}}				float avgDist = result.sumDist[i]/(numStars-1);
				if (avgDist<minAvgDist) {{
					idealCapitalStars.clear();
					Integer star = new Integer(i);
					idealCapitalStars.add(star);
					minAvgDist = avgDist;
				}
}				else {if (avgDist==minAvgDist) {{
					Integer star = new Integer(i);
					idealCapitalStars.add(star);
				}
}}			}
		}
		public void displayResults() {
			long finalTime = System.currentTimeMillis();
			System.out.println();
			System.out.println(closestDist+" // minimal pairwise distance");
			for (int i = 0; i<closestNeighbors.size(); i++){
				Integer[] neighbors = (Integer[])closestNeighbors.elementAt(i);
				System.out.println("("+stars[neighbors[0].intValue()].toString()+") ("+stars[neighbors[1].intValue()].toString()+")");
			}
			System.out.println();
			System.out.println(farthestDist+" // maximal pairwise distance");
			for (int i = 0; i<farthestNeighbors.size(); i++){
				Integer[] neighbors = (Integer[])farthestNeighbors.elementAt(i);
				System.out.println("("+stars[neighbors[0].intValue()].toString()+") ("+stars[neighbors[1].intValue()].toString()+")");
			}
			System.out.println();
			System.out.println(minMaxDist+" // minimum maximal distance");
			for (int i = 0; i<idealHubStars.size(); i++){
				Integer[] neighbors = (Integer[])idealHubStars.elementAt(i);
				System.out.println("("+stars[neighbors[0].intValue()].toString()+") ("+stars[neighbors[1].intValue()].toString()+")");
			}
			System.out.println();
			System.out.println(maxMinDist+" // maximum minimal distance");
			for (int i = 0; i<idealJailStars.size(); i++){
				Integer[] neighbors = (Integer[])idealJailStars.elementAt(i);
				System.out.println("("+stars[neighbors[0].intValue()].toString()+") ("+stars[neighbors[1].intValue()].toString()+")");
			}
			System.out.println();
			System.out.println(minAvgDist+" // minimal average distance");
			for (int i = 0; i<idealCapitalStars.size(); i++){
				Integer capitalStar = (Integer)idealCapitalStars.elementAt(i);
				System.out.println("("+stars[capitalStar.intValue()].toString()+")");
			}
			System.out.println();
			long runningTime = finalTime-initialTime;
			double avrgWorkerTime = sumWorkerTime/NUM_WORKERS;
			System.out.println("Total execution time: "+runningTime+" ms");
			System.out.println("(Worker avrg: "+avrgWorkerTime+" ms, Overhead: "+(runningTime-avrgWorkerTime)+" ms)");
		}
	}
}