package salsa.examples.mapreduce;

import java.io.Serializable;
import java.util.Vector;

import salsa.examples.mapreduce.Context;
import salsa.examples.mapreduce.Reducer;
import salsa.examples.mapreduce.DoubleIntPair;


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
