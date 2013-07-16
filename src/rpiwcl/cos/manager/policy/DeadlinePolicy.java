package rpiwcl.cos.manager.policy;

import rpiwcl.cos.util.Utility;
import rpiwcl.cos.manager.policy.Policy;

public class DeadlinePolicy extends Policy {
    private double tRelDeadline;
    
    public DeadlinePolicy( double tDeadline ) {
        this.tRelDeadline = tDeadline;
    }

    public void start() {
        this.tDeadline = this.tRelDeadline + (double)System.currentTimeMillis() / 1000;
        System.out.println( "[Deadline] start deadline measurement (rel=" + tRelDeadline + ", abs=" + tDeadline  + ")" );
    }

    public int[] schedule( ThroughputPredictor tp, 
                           int tasksDone, int Ncurr, int Mcurr, double budgetCurr ) {
        double tCurr = (double)System.currentTimeMillis() / 1000;

        Utility.debugPrint( "[Deadline] schedule, input parameters:" );
        Utility.debugPrint( "    tDeadline=" + tDeadline );
        Utility.debugPrint( "    tCurr    =" + tCurr );
        Utility.debugPrint( "    tasks    =" + tasks );
        Utility.debugPrint( "    tasksDone=" + tasksDone );
        Utility.debugPrint( "    Ncurr    =" + Ncurr );
        Utility.debugPrint( "    Nmax   =" + Nmax );
        Utility.debugPrint( "    Mcurr    =" + Mcurr );

        if (tDeadline < tCurr) {
            System.err.println( "[Deadline] deadline already passed" );
            return null;
        }

        int N = Ncurr;
        int M = Mcurr;
        
        double tpTarget = (double)(tasks - tasksDone) / (tDeadline - tCurr);
        double tpPredict = tp.predict( N, M );
        System.out.println( "[Deadline] tpTarget=" + tpTarget + ", tpPredict=" + tpPredict );
        
        if (tpPredict < tpTarget) {
            do {
                if (N < Nmax)
                    N++;
                else
                    M++;
            } while( tp.predict( N, M ) < tpTarget );
        }
        else if (tpPredict > tpTarget) {
            boolean flag = false;
            do {
                if (0 < M)
                    M--;
                else {
                    N--;
                    flag = true;
                }
            } while (tp.predict( N, M ) < tpTarget);
            
            if (flag)
                M++;
            else
                N--;
        }

        System.out.println( "[Deadline] N=" + N + ", M=" + M + ", tpPredict=" + tp.predict(N, M)  );

        int[] result = new int[2];
        result[0] = N;
        result[1] = M;

        return result;
    }
}
