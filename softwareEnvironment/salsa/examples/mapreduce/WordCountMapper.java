package salsa.examples.mapreduce;

import java.io.Serializable;

import salsa.examples.mapreduce.Context;
import salsa.examples.mapreduce.Mapper;


public class WordCountMapper implements Mapper, Serializable {

    public void map( String text, Context context ) {
        System.out.println( "map, text=" + text );
        context.write( text, new Integer( 1 ) );
    }

}
