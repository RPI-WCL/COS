package rpiwcl.cos.test.salsa.mapreduce;

import java.io.Serializable;
import java.util.Vector;

import rpiwcl.cos.test.salsa.mapreduce.Context;
import rpiwcl.cos.test.salsa.mapreduce.Reducer;


public class WordCountReducer implements Reducer, Serializable {

    public void reduce( String key, Vector value, Context context ) {
        // System.out.println( "reduce, key=" + key + ", value=" + value );
        
        int valueSize = value.size();
        int sum = 0;
        for (int i = 0; i < valueSize; i++) {
            Integer v = (Integer)value.get( i );
            sum += v.intValue();
        }

        context.write( key, new Integer( sum ) );
    }

}
