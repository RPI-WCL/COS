package rpiwcl.cos.manager.policy;

import rpiwcl.cos.manager.policy.Policy;

public class BudgetPolicy extends Policy {

    public BudgetPolicy( double budget ) {
        this.budget = budget;
    }

    public void start() {
    }
    
    public int[] schedule( ThroughputPredictor tp, 
                           int tasksDone, int Ncurr, int Mcurr, double budgetCurr ) {
        int[] result = new int[2];
        result[0] = Ncurr;
        result[1] = Mcurr;

        return result;        
    }
}
