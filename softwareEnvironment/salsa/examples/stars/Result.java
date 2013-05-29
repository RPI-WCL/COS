/*
 * Author:		Shigeru Imai (RCSID:660855993)
 * Filename:	Result.java
 * Date:		Nov 11, 2010
 * 
 */

package salsa.examples.stars;

import java.io.Serializable;
import java.util.Vector;


public class Result implements Serializable {
	// used for Ideal hub stars and farthest neighbors
	public Vector[] maxDistStars;
	public float[] maxDist;	

	// used for Ideal jail stars and closest neighbors	
	public Vector[] minDistStars;
	public float[] minDist;

	// used for Ideal capital stars
	public float[] sumDist;
	
	private int numStars;
    private long elapsedTime;


	public Result( int numStars ) {
		maxDistStars = new Vector[numStars];
		maxDist = new float[numStars];
		for (int i = 0; i < numStars; i++) {
			maxDistStars[i] = new Vector();
			maxDist[i] = 0.0F;
		}

		minDistStars = new Vector[numStars];
		minDist = new float[numStars];		
		for (int i = 0; i < numStars; i++) {
			minDistStars[i] = new Vector();
			minDist[i] = Float.MAX_VALUE;
		}

		sumDist = new float[numStars];
		for (int i = 0; i < numStars; i++)
			sumDist[i] = 0.0F;

		this.numStars = numStars;
        this.elapsedTime = 0;
	}

    public int getNumStars() {
        return numStars;
    }
    
    public void setElapsedTime( long elapsedTime ) {
        this.elapsedTime = elapsedTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

}
