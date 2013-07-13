package rpiwcl.cos.test.salsa.mapreduce;

import java.io.Serializable;
import java.util.Vector;

import rpiwcl.cos.test.salsa.mapreduce.Context;
import rpiwcl.cos.test.salsa.mapreduce.Reducer;
import rpiwcl.cos.test.salsa.mapreduce.DoubleIntPair;


public class MeanTempReducer implements Reducer, Serializable {

    public void reduce( String key, Vector values, Context context ) {
        double sum = 0.0;
        int count = 0;

        for (int i = 0; i < values.size(); i++) {
            DoubleIntPair pair = (DoubleIntPair)values.get( i );
            sum += pair.getFirst();
            count += pair.getSecond();
        }
        context.write( key, new Double( sum / count ) );
    }

}
