package rpiwcl.cos.manager.policy;

import java.util.*;
import Jama.Matrix;
import rpiwcl.cos.manager.policy.ThroughputSample;
import rpiwcl.cos.util.Utility;

public class ThroughputPredictor {
    public static final int POLICY_NO_LIMIT = 0;
    public static final int POLICY_LIMIT_PAST_SAMPLE_SIZE = 1;
    public static final int POLICY_LIMIT_PAST_SAMPLE_TIME = 2;

    private static final boolean ADD_RANDOM_VALUE = false;
    private static final double SAMPLE_RANDOMNESS_RANGE = 0.01;

    private int policy;
    private long limit;  // size or time[ms] depending on the policy
    private LinkedList<ThroughputSample> samples; // (N, M, tp, time)


    public ThroughputPredictor( int policy, long limit ) {
        this.policy = policy;
        this.limit = limit;
        this.samples = new LinkedList<ThroughputSample>();
    }

    public void addSample( int N, int M, double tp ) {
        if ((policy == POLICY_LIMIT_PAST_SAMPLE_SIZE) && (samples.size() == (int)limit))
            samples.pop();
        else if(policy == POLICY_LIMIT_PAST_SAMPLE_TIME)
            // TODO: support POLICY_LIMIT_PAST_SAMPLE_TIME
            System.out.println( "[TpPred] POLICY_LIMIT_PAST_SAMPLE_TIME not suppoted yet" );
        
        ThroughputSample sample = new ThroughputSample( N, M, tp );
        samples.push( sample );
    }


    public double predict( int N, int M ) {
        double[][] xVals = new double[samples.size()][3];
        double[][] yVals = new double[samples.size()][1];

        for (int i = 0; i < samples.size(); i++) {
            ThroughputSample sample = (ThroughputSample)samples.get( i );

            if (ADD_RANDOM_VALUE) {
                // add small random value to avoid creating singular matrix
                xVals[i][0] = sample.getN() + sample.getN() * 
                    SAMPLE_RANDOMNESS_RANGE * Math.random() - (SAMPLE_RANDOMNESS_RANGE/2);
                xVals[i][1] = sample.getM() + sample.getM() *
                    SAMPLE_RANDOMNESS_RANGE * Math.random() - (SAMPLE_RANDOMNESS_RANGE/2);
                xVals[i][2] = 1.0 + 1.0 *
                    SAMPLE_RANDOMNESS_RANGE * Math.random() - (SAMPLE_RANDOMNESS_RANGE/2);
                yVals[i][0] = sample.getTp() + sample.getTp() *
                    SAMPLE_RANDOMNESS_RANGE * Math.random() - (SAMPLE_RANDOMNESS_RANGE/2);
            }
            else {
                xVals[i][0] = sample.getN();
                xVals[i][1] = sample.getM();
                xVals[i][2] = 1.0;
                yVals[i][0] = sample.getTp();
            }
        }
        Matrix X = new Matrix( xVals );
        Matrix Y = new Matrix( yVals );

        Matrix w = X.transpose().times( X ).inverse().times( X.transpose() ).times( Y );
        double tp = w.get(0, 0) * N + w.get(1, 0) * M + w.get(2, 0);

        Utility.debugPrint( "[TpPred] w0=" + w.get(0,0) + ", w1=" + w.get(1,0) + ", w2=" + w.get(2,0) );

        return tp;
    }


    public static void main( String[] args ) {
        ThroughputPredictor tpPredictor = 
            new ThroughputPredictor( ThroughputPredictor.POLICY_NO_LIMIT, 0 );

        try {
            tpPredictor.addSample( 1, 0, 73627.91369000223 );
        } catch (Exception ex) {
            System.err.println( ex );
        }

        try {
            tpPredictor.addSample( 1, 0, 72319.7871515398 );
        } catch (Exception ex) {
            System.err.println( ex );
        }

        try {
            tpPredictor.addSample( 1, 0, 52717.84946724149 );
            System.out.println( tpPredictor.predict( 1, 0 ) );
        } catch (Exception ex) {
            System.err.println( ex );
        }

        // tpPredictor.addSample( 2, 0, 19647.421 );
        // tpPredictor.addSample( 2, 2, 23589.025 );
        // tpPredictor.addSample( 2, 4, 42043.005 );
        // tpPredictor.addSample( 2, 6, 58351.578 );
        // tpPredictor.addSample( 2, 8, 58878.421 );
        // tpPredictor.addSample( 2, 10, 91458.465 );

        // System.out.println( tpPredictor.predict( 2, 0 ) );
        // System.out.println( tpPredictor.predict( 2, 2 ) );
        // System.out.println( tpPredictor.predict( 2, 4 ) );
        // System.out.println( tpPredictor.predict( 2, 6 ) );
        // System.out.println( tpPredictor.predict( 2, 8 ) );
        // System.out.println( tpPredictor.predict( 2, 10 ) );
    }

}
        
    
