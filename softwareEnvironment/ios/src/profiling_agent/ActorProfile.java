package src.profiling_agent;

import java.util.Hashtable;
import java.util.Vector;

import salsa.naming.UAN;
import salsa.language.ActorReference;
import salsa.language.UniversalActor;

public class ActorProfile {
	private UAN uan;
	private Hashtable targetList = new Hashtable();
	private Hashtable sourceList = new Hashtable();

	private long processed = 0;
	private long timeProcessing = 0;
	private long received = 0;
	private long sent = 0;
	private long timeSending = 0;
	private long lastResetTime = System.currentTimeMillis();
	private long memUsed = 0;

	public long processed()			{ return processed; }
	public long timeProcessing()		{ return timeProcessing; }
	public long received()			{ return received; }
	public long sent()			{ return sent; }
	public long timeSending()		{ return timeSending; }
	public long lastResetTime()		{ return lastResetTime; }
	public UAN getUAN()			{ return uan; }
	public long numTargets()		{ return targetList.size(); }
	public long numSources()		{ return sourceList.size(); }

	public ActorProfile(UAN uan) {
		this.uan = uan;
	}

	public void reset() {
		processed = 0;
		timeProcessing = 0;
		received = 0;
		sent = 0;
		timeSending = 0;
		lastResetTime = System.currentTimeMillis();

		sourceList = new Hashtable();
		targetList = new Hashtable();
	}

	public long getTotalReferences() {
		return sourceList.keySet().size() + targetList.keySet().size();
	}
	public long getLocalReferences(String local_location) {
		Vector sources = new Vector(sourceList.keySet());
		sources.addAll(targetList.keySet());

		long local_references = 0;
		for (int i = 0; i < sources.size(); i++) {
			UAN current = (UAN)sources.get(i);
			ActorReference reference = UniversalActor.getReferenceByName(current);

			if ( local_location.equals(reference.getUAL()) ) local_references++;
		}
		return local_references;
	}
	public void setMemUsed(long memUsed) {
		this.memUsed = memUsed;
	}
	public long getMemUsed() {
		return memUsed;
	}

	public void print() {
		System.out.println("candidate: " + uan);
		Vector targets = new Vector(targetList.keySet());
		System.out.println("targets:");
		for (int i = 0; i < targets.size() ; i++) {
			System.out.println("\t\t" + targets.get(i) + " at " + targetList.get(targets.get(i)));
		}

		Vector sources = new Vector(sourceList.keySet());
		System.out.println("sources:");
		for (int i = 0; i < sources.size() ; i++) {
			System.out.println("\t\t" + sources.get(i) + " at " + sourceList.get(sources.get(i)));
		}
	}


	/********
		*	The following methods handle profiling information about processing messages
	*********/
	long beginProcessingTime = 0;
	public void processedMessage()		{ processed++; }
	public void beginProcessing()		{ beginProcessingTime = System.currentTimeMillis(); }
	public void endProcessing()		{ timeProcessing += System.currentTimeMillis()-beginProcessingTime; }


	/********
		*	The following methods handle profiling information about sending and receiving messages
	 ********/
	long beginSendingTime = 0;
	public void beginSending()		{ beginSendingTime = System.currentTimeMillis(); }
	public void endSending()		{ timeSending += System.currentTimeMillis()-beginSendingTime; }

	/********
		*	Return the number of messages received by another actor
	 ********/
	public int receivedFrom(UAN sourceUAN)	{ return get(sourceList, sourceUAN); }

	/********
		*	Return the number of messages sent to another actor
	 ********/
	public int sentTo(UAN targetUAN) 	{ return get(targetList, targetUAN); }

	/********
		*	Helper function for receivedFrom and sentTo
	 ********/
	public int get(Hashtable hashtable, UAN uan) {
		Object result = hashtable.get(uan.toString());
		if (result != null) return ((Integer)result).intValue();
		return 0;
	}

	/********
		*	Increment the number of messages received by another actor
	 ********/
	public void incrementReceived(UAN sourceUAN) {
		if (sourceUAN == null) return;
		received++;
		increment(sourceList, sourceUAN);
	}

	/********
		*	Increment the number of messages send to another actor
	 ********/
	public void incrementSent(UAN targetUAN) {
		if (targetUAN == null) return;
		sent++;
		increment(targetList, targetUAN);
	}
	
	/********
		*	Helper function for incrementSent and incrementReceived
	 ********/
	public void increment(Hashtable hashtable, UAN uan) {
		Object result = hashtable.get(uan.toString());
		if (result != null) result = new Integer( ((Integer)result).intValue() + 1 );
		else result = new Integer(1);

		hashtable.put(uan.toString(), result);
	}

	/********
	 	*	Returns the amount of communication exchanged with the given actor
	 ********/
	public double getCommWith(UAN location) {
		return (double)(receivedFrom(location) + sentTo(location));
	}
}
