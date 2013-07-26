package rpiwcl.cos.test.salsa.trapezoid2;

public class TrapIntegral {
    private double local_a;
    private double local_b;
    private double h;


    public TrapIntegral() {
        local_a = 0;
        local_b = 0;
        h = 0;
    }


    void setParameters( double local_a, double local_b, double h ) {
        this.local_a = local_a;
        this.local_b = local_b;
        this.h = h;
    }


    double startIntegral() {
        return (f(local_a) + f(local_b))/2;
    }


    double add( int i ) {
        double x = local_a + i * h;
        return (f(x) * h);
    }


    double f( double x ) {
        double f = 0.0;

        for (int i = 0; i < 1000; i++)
            f += 10 * Math.sin(x) / Math.cos(x);
        
        return f;
    }
        
}


        
        
    

    
