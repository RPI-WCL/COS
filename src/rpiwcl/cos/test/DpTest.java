package rpiwcl.cos.test;

import java.util.*;

public class DpTest {
    private static final int M1_SMALL = 1;
    private static final int M1_MEDIUM = 2;
    private static final int M1_LARGE = 3;
    private static final int M1_XLARGE = 4;
    private static final int M3_XLARGE = 5;
    private static final int M3_2XLARGE = 6;
    private static final int C1_MEDIUM = 7;
    private static final int C1_XLARGE = 8;
    private static final int CC2_8XLARGE = 9;
    private static final int NUM_INSTANCE_TYPES = 9;
    
    //                                  0     1     2     3    4     5     6      7    8
    private static double[] prices = {0.06, 0.12, 0.24, 0.48, 0.5, 1.00, 0.145, 0.58, 1.3};
    private static double[] ecus =   {    1,   2,    4,    8,  13,   26,     5,   20,  88};
    private double wcu;
    private int workers;

    public DpTest( double wcu, int workers ) {
        this.wcu = wcu;
        this.workers = workers;
    }


    public void compute() {
        double[] cost = new double[workers+1];
        Arrays.fill( cost, 0.0 );

        for (int i = 1; i <= workers; i++) {
            double q = Double.MAX_VALUE;
            System.out.println( "i=" + i + "------------------------------");

            for (int j = 0; j < NUM_INSTANCE_TYPES; j++) {
                System.out.print( " j=" + j + ", cost=" + prices[j] + 
                                  ", ecu=" + ecus[j] + " --> ");

                if (wcu < (ecus[j] / i)) {
                    // all i workers are qualified
                    System.out.println( "A:assign " + i + " workers, cost=" + prices[j] );
                    q = Math.min( q, prices[j] );
                } 
                else if (0 < Math.floor( ecus[j] / wcu )) {
                        // [ecus[j]/w] workers are qualified, but the rest are not
                        int k = i - (int)Math.floor( ecus[j] / wcu );
                        System.out.println( "B:assign " + (int)Math.floor( ecus[j] / wcu ) +
                                            " workers, prices[" + j + "](" + prices[j] + 
                                            ") +  cost[" + k + "](" + cost[k] + ")=" + 
                                            (prices[j] + cost[k]) );
                        q = Math.min( q, prices[j] + cost[k] );
                }
                else {
                    // all i workers are not qualified
                    System.out.println( "C: N/A" );
                }
            }
            cost[i] = q;
            System.out.println( "cost[" + i + "]=" + cost[i] );
            System.out.println();
        }
        
        System.out.println( "result =" + cost[workers] );
    }


    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println( "Usage: java DpTest <WCU_target> <#workers>" );
            return;
        }

        DpTest dp = new DpTest( Double.parseDouble(args[0]), Integer.parseInt(args[1]) );
        dp.compute();
    }
}
