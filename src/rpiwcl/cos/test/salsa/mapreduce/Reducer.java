package rpiwcl.cos.test.salsa.mapreduce;

import java.util.Vector;
import rpiwcl.cos.test.salsa.mapreduce.Context;


public interface Reducer {
    public void reduce( String key, Vector value, Context context );
}
