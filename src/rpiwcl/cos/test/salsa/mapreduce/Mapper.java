package rpiwcl.cos.test.salsa.mapreduce;

import rpiwcl.cos.test.salsa.mapreduce.Context;


public interface Mapper {
    public void map( String text, Context context );
}
