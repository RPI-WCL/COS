package salsa.examples.mapreduce;

import java.io.Serializable;
import java.util.Vector;

import salsa.examples.mapreduce.Context;
import salsa.examples.mapreduce.Reducer;


public class WordCountReducer implements Reducer, Serializable {

    public void reduce( String key, Vector value, Context context ) {
        System.out.println( "reduce, key=" + key + ", value=" + value );
        
        int valueSize = value.size();
        int sum = 0;
        for (int i = 0; i < valueSize; i++) {
            Integer v = (Integer)value.get( i );
            sum += v.intValue();
        }

        context.write( key, new Integer( sum ) );
    }

}
