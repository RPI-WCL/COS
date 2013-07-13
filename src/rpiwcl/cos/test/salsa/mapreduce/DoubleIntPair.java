package rpiwcl.cos.test.salsa.mapreduce;

import java.io.Serializable;


public class DoubleIntPair implements Serializable {
    private double first = 0.0;
    private int second = 0;

    public DoubleIntPair(double first, int second) {
        this.first = first;
        this.second = second;
    }
        
    public void set(double first, int second) {
        this.first = first;
        this.second = second;
    }
    public double getFirst() {
        return first;
    }
    public int getSecond() {
        return second;
    }
    public String toString() {
        String str = "first=" + first + ", second=" + second;
        return str;
    }
}
