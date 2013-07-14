package rpiwcl.cos.manager.policy;

public abstract class Policy {
    // variable names follow the UCC2013 paper convention
    protected double tDeadline;         // [msec] --> constraint
    protected double budget;            // [USD] --> constraint
    protected int tasks;
    protected int Nlimit;
    protected double cost;              // [USD/hour] 
    protected int numWorkers;
    
    abstract public int[] schedule( ThroughputPredictor tp, 
                                    int tasksDone, int Ncurr, int Mcurr, double budgetCurr );

    abstract public void start();

    public void setTasks( int tasks ) {
        this.tasks = tasks;
    }

    public void setNlimit( int Nlimit ) {
        this.Nlimit = Nlimit;
    }

    public void setCost( double cost ) {
        this.cost = cost;
    }

    public void setNumWorkers( int numWorkers ) {
        this.numWorkers = numWorkers;
    }
}
