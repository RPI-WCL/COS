package salsa.examples.mapreduce;

import salsa.examples.mapreduce.Context;

public interface Mapper {
    public void map( String text, Context context );
}
