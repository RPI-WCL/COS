package gctest.global;

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


public class FourHostQueens extends UniversalActor  {
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
		FourHostQueens instance = (FourHostQueens)new FourHostQueens(uan, ual,null).construct();
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

	public static ActorReference getReferenceByName(UAN uan)	{ return new FourHostQueens(false, uan); }
	public static ActorReference getReferenceByName(String uan)	{ return FourHostQueens.getReferenceByName(new UAN(uan)); }
	public static ActorReference getReferenceByLocation(UAL ual)	{ return new FourHostQueens(false, ual); }

	public static ActorReference getReferenceByLocation(String ual)	{ return FourHostQueens.getReferenceByLocation(new UAL(ual)); }
	public FourHostQueens(boolean o, UAN __uan)	{ super(false,__uan); }
	public FourHostQueens(boolean o, UAL __ual)	{ super(false,__ual); }
	public FourHostQueens(UAN __uan,UniversalActor.State sourceActor)	{ this(__uan, null, sourceActor); }
	public FourHostQueens(UAL __ual,UniversalActor.State sourceActor)	{ this(null, __ual, sourceActor); }
	public FourHostQueens(UniversalActor.State sourceActor)		{ this(null, null, sourceActor);  }
	public FourHostQueens()		{  }
	public FourHostQueens(UAN __uan, UAL __ual, Object obj) {
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
			    createRemotely(__uan, __ual, "gctest.global.FourHostQueens", sourceRef);
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

	public UniversalActor construct (int size, int mask, int count) {
		Object[] __arguments = { new Integer(size), new Integer(mask), new Integer(count) };
		this.send( new Message(this, this, "construct", __arguments, null, null) );
		return this;
	}

	public UniversalActor construct() {
		Object[] __arguments = { };
		this.send( new Message(this, this, "construct", __arguments, null, null) );
		return this;
	}

	public class State extends UniversalActor .State {
		public FourHostQueens self;
		public void updateSelf(ActorReference actorReference) {
			((FourHostQueens)actorReference).setUAL(getUAL());
			((FourHostQueens)actorReference).setUAN(getUAN());
			self = new FourHostQueens(false,getUAL());
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
			addClassName( "gctest.global.FourHostQueens$State" );
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

		int SIZE, MASK, COUNT, TOTALACTOR;
		public void construct(int size, int mask, int count){
			SIZE = size;
			MASK = mask;
			COUNT = count;
		}
		public int getCount() {
			return COUNT;
		}
		public void add(int x) {
			COUNT += x;
			TOTALACTOR--;
			if (TOTALACTOR==0) {{
				{
					// ((FourHostQueens)self)<-show(new Integer(COUNT))
					{
						Object _arguments[] = { new Integer(COUNT) };
						Message message = new Message( self, ((FourHostQueens)self), "show", _arguments, null, null );
						__messages.add( message );
					}
				}
			}
}		}
		public void show(Object data) {
			System.out.println(data);
			System.exit(0);
		}
		public void compute(Integer y, Integer left, Integer down, Integer right) {
			this.Backtrack(y.intValue(), left.intValue(), down.intValue(), right.intValue());
		}
		public void Backtrack(int y, int left, int down, int right) {
			int bitmap, bit;
			if (y==SIZE) {{
				COUNT++;
			}
}			else {{
				bitmap = MASK&~(left|down|right);
				while (bitmap>0) {
					bit = -bitmap&bitmap;
					bitmap ^= bit;
					this.Backtrack(y+1, (left|bit)<<1, down|bit, (right|bit)>>1);
				}
			}
}		}
		public void act(String args[]) {
			try {
				SIZE = Integer.parseInt(args[0]);
				COUNT = 0;
				MASK = (1<<SIZE)-1;
				TOTALACTOR = 0;
				if (SIZE<6) {{
					{
						Token token_4_0 = new Token();
						Token token_4_1 = new Token();
						// ((FourHostQueens)self)<-compute(0, 0, 0, 0)
						{
							Object _arguments[] = { new Integer(0), new Integer(0), new Integer(0), new Integer(0) };
							Message message = new Message( self, ((FourHostQueens)self), "compute", _arguments, null, token_4_0 );
							__messages.add( message );
						}
						// ((FourHostQueens)self)<-getCount()
						{
							Object _arguments[] = {  };
							Message message = new Message( self, ((FourHostQueens)self), "getCount", _arguments, token_4_0, token_4_1 );
							__messages.add( message );
						}
						// ((FourHostQueens)self)<-show(token)
						{
							Object _arguments[] = { token_4_1 };
							Message message = new Message( self, ((FourHostQueens)self), "show", _arguments, token_4_1, null );
							__messages.add( message );
						}
					}
				}
}				else {{
					String[] hostArray = new String[4];
					String nameServer = args[1];
					for (int i = 0; i<4; i++){
						hostArray[i] = args[2+i];
					}
					int down = 0, left = 0, right = 0;
					int jdown = 0, jleft = 0, jright = 0;
					int bitmap = (MASK&~(left|down|right));
					while (bitmap>0) {
						int bit = -bitmap&bitmap;
						bitmap ^= bit;
						jdown = down|bit;
						jleft = (left|bit)<<1;
						jright = (right|bit)>>1;
						int jbitmap = MASK&~(jleft|jdown|jright);
						while (jbitmap>0) {
							bit = -jbitmap&jbitmap;
							jbitmap ^= bit;
							TOTALACTOR++;
							FourHostQueens nq = ((FourHostQueens)new FourHostQueens(new UAN("uan://"+nameServer+"/FHQ/"+TOTALACTOR), new UAL("rmsp://"+hostArray[TOTALACTOR%4]+"/FHQ/"+TOTALACTOR),this).construct(SIZE, MASK, 0));
							{
								Token token_6_0 = new Token();
								Token token_6_1 = new Token();
								// nq<-Backtrack(new Integer(2), new Integer((jleft|bit)<<1), new Integer(jdown|bit), new Integer((jright|bit)>>1))
								{
									Object _arguments[] = { new Integer(2), new Integer((jleft|bit)<<1), new Integer(jdown|bit), new Integer((jright|bit)>>1) };
									Message message = new Message( self, nq, "Backtrack", _arguments, null, token_6_0 );
									__messages.add( message );
								}
								// nq<-getCount()
								{
									Object _arguments[] = {  };
									Message message = new Message( self, nq, "getCount", _arguments, token_6_0, token_6_1 );
									__messages.add( message );
								}
								// add(token)
								{
									Object _arguments[] = { token_6_1 };
									Message message = new Message( self, self, "add", _arguments, token_6_1, null );
									__messages.add( message );
								}
							}
						}
					}
				}
}			}
			catch (Exception e) {
				System.err.println(e);
				System.err.println("USAGE:java -cp <lib> gctest.global.FourHostQueens  <num> <NameSvc> <host1> <host2> <host3> <host4>");
			}

		}
	}
}