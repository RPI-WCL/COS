package src.profiling_agent;

import java.util.Hashtable;
import java.util.Vector;

import salsa.language.Message;
import salsa.language.ActorReference;
import salsa.language.UniversalActor;
import salsa.naming.UAN;
import salsa.naming.UAL;


public class ATSProfilingAgent implements ProfilingAgent {

	private int			maxMemory = 0;
	private int			processors = 0;
	private double			threshold = 0.3;
	private	double			cpuSpeed = 0;
	public	double			getCpuSpeed()			{ return cpuSpeed; }
	public	boolean 		isLight()			{ return getAvailableProcessing() > (cpuSpeed*threshold); }

	private	long			lastResetTime = System.currentTimeMillis();
	public	long			timeSinceLastReset()		{ return System.currentTimeMillis() - lastResetTime; }

	private	boolean			computing = true;
	public	boolean			isComputing()			{ return computing; }
	public	void			setComputing(boolean computing)	{ this.computing = computing; }

	public ATSProfilingAgent() {
		if ( System.getProperty("silent") == null) {
			System.err.println("Determining cpu performance:");
		}

		String processing = System.getProperty("cpu");
		if (processing != null) {
			cpuSpeed = Integer.parseInt(processing);
		} else {
			cpuSpeed = 1;
		}
		String procs = System.getProperty("procs");
		if (procs != null) {
			processors = Integer.parseInt(procs);
		} else {
			processors = 1;
		}
		String maxMem = System.getProperty("memory");
		if (maxMem != null) {
			maxMemory = Integer.parseInt(maxMem);
		} else {
			maxMemory = 1;
		}
	}

	public String gather_observation(ActorReference protocolActor) {
		String response = "";

		response += processedSinceStart + "\t" + cpuSpeed + "\t" + processors + "\t" + maxMemory + "\t";
		Vector candidates = getActors();

		String here = protocolActor.getUAL().getLocation();
		long mem_used = 0;
		long local_references = 0;
		long remote_references = 0;
		long number_actors = candidates.size();
		for (int i = 0; i < candidates.size(); i++) {
			ActorProfile current = (ActorProfile)candidates.get(i);
			mem_used += current.getMemUsed();

//			long references_to_here = current.getLocalReferences(here);
//			local_references += references_to_here;
//			remote_references += current.getTotalReferences() - references_to_here;
		}
		response += mem_used + "\t" + local_references + "\t" + remote_references + "\t" + number_actors;
		return response;
	}


	private Hashtable actorProfiles = new Hashtable();
	public Vector getActors() {
		Vector uans = new Vector(actorProfiles.keySet());

//		Vector actors = new Vector();
//		for (int i = 0; i < uans.size(); i++) {
//			actors.add(UniversalActor.getReferenceByName((UAN)uans.get(i)));
//		}
		return uans;
	}

	private long totalProcessed = 0;
	private long processedSinceStart = 0;

	public double getAvailableProcessing() {
		Vector profiles = getActorProfiles();

		double usedFLOPS = 0;
		for (int i = 0; i < profiles.size(); i++) {
			ActorProfile candidate = (ActorProfile)profiles.get(i);

			if (candidate.timeProcessing() > candidate.timeSending()) {
				usedFLOPS += cpuSpeed*((double)(candidate.timeProcessing()-candidate.timeSending())/timeSinceLastReset());
			}
		}
		return cpuSpeed-usedFLOPS;
	}

	public double getFLOPSUsedBy(ActorProfile candidate) {
		Vector profiles = getActorProfiles();

		double totalProcessingTime = 0;
		for (int i = 0; i < profiles.size(); i++) {
			ActorProfile current = (ActorProfile)profiles.get(i);

			if (current.timeProcessing() > current.timeSending()) {
				totalProcessingTime += (double)(current.timeProcessing()-current.timeSending())/(double)timeSinceLastReset();
			}
		}
		double candidateTime = 0;
		if (candidate.timeProcessing() > candidate.timeSending()) {
			candidateTime = (double)(candidate.timeProcessing()-candidate.timeSending())/(double)timeSinceLastReset();
		}
		return cpuSpeed * (candidateTime/totalProcessingTime);
	}

	Vector stealSources = new Vector();

	public int getDistinctSteals() {
		return stealSources.size();
	}

	public void gotStealFrom(String source) {
		if (!stealSources.contains(source)) stealSources.add(source);
	}


	public long processed()				{ return totalProcessed; }

	public Vector getActorProfiles() 		{ return new Vector(actorProfiles.values()); }
	public Vector getActorReferences() {
		Vector profiles = getActorProfiles();
		Vector references = new Vector();

		for (int i = 0; i < profiles.size(); i++) {
			references.add( UniversalActor.getReferenceByName( ((ActorProfile)profiles.get(i)).getUAN()) );
		}
		return references;
	}

	public void beginProcessed(UAN identifier) {
		ActorProfile target = (ActorProfile)actorProfiles.get(identifier);
		if (target == null) return;  
    
		target.processedMessage();
		target.beginProcessing();
	}
	public void endProcessed(UAN identifier) { 
		totalProcessed++;
		processedSinceStart++;

		ActorProfile target = (ActorProfile)actorProfiles.get(identifier);
		if (target == null) return;  
		target.endProcessing();
	}

	public void setMemUsed(UAN targetUAN, long memUsed) {
		ActorProfile target = (ActorProfile)actorProfiles.get(targetUAN);
		if (target == null) return;
		target.setMemUsed( memUsed );
	}

	public void received(UAN targetUAN, UAN sourceUAN) {
		ActorProfile target = (ActorProfile)actorProfiles.get(targetUAN);
		if (target == null) return;
		target.incrementSent( sourceUAN );
	}

	public void sent(UAN sourceUAN, UAN targetUAN) {
		ActorProfile source = (ActorProfile)actorProfiles.get(sourceUAN);
		if (source == null) return;
		source.incrementSent( targetUAN );
	}

	public void beginSend(UAN sourceUAN, UAN targetUAN) {
		ActorProfile source = (ActorProfile)actorProfiles.get(sourceUAN);
		if (source == null) return;
		source.beginSending();
	}

	public void endSend(UAN sourceUAN, UAN targetUAN) {
		ActorProfile source = (ActorProfile)actorProfiles.get(sourceUAN);
		if (source == null) return;
		source.incrementSent( targetUAN );
		source.endSending();
	}

	/*
		The following create new actor profiles and manage their
		entering and exiting the theater
	*/
	public void addProfile(UAN identifier) 		{
		System.err.println("ADDING ACTOR PROFILE: " + identifier);
		actorProfiles.put(identifier, new ActorProfile(identifier));
	}
	public void removeProfile(UAN identifier) 	{ actorProfiles.remove(identifier); }

	public void reset_all() {
		this.reset();
		Vector profiles = getActorProfiles();
		for (int i = 0; i < profiles.size(); i++) { ((ActorProfile)profiles.get(i)).reset(); }
	}

	public void reset() {
		totalProcessed = 0;
		lastResetTime = System.currentTimeMillis();
		stealSources.clear();
	}

	public long totalProcessed() { return processedSinceStart; }
}
