package rpiwcl.cos.test.salsa.mapreduce;

import java.io.Serializable;
import java.util.StringTokenizer;

import rpiwcl.cos.test.salsa.mapreduce.Context;
import rpiwcl.cos.test.salsa.mapreduce.Mapper;


public class WordCountMapper implements Mapper, Serializable {
    private final static Integer one = new Integer( 1 );

    public void map( String text, Context context ) {
        StringTokenizer it = new StringTokenizer( text );

        while ( it.hasMoreTokens() ) {
            context.write( it.nextToken(), one );
        }
    }

}
