package salsa.examples.mapreduce;

import java.util.Vector;
import salsa.examples.mapreduce.Context;

public interface Reducer {
    public void reduce( String key, Vector value, Context context );
}
