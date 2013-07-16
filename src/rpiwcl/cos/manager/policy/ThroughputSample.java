package rpiwcl.cos.manager.policy;


public class ThroughputSample {
    private int N;
    private int M;
    private double tp; // throughput
    private long time;

    public ThroughputSample( int N, int M, double tp ) {
        this.N = N;
        this.M = M;
        this.tp = tp;
        this.time = System.currentTimeMillis();
    }

    public int getN() {
        return N;
    }

    public int getM() {
        return M;
    }

    public double getTp() {
        return tp;
    }

    public long getTime() {
        return time;
    }
}

    
