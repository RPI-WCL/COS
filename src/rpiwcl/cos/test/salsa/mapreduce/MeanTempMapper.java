package rpiwcl.cos.test.salsa.mapreduce;

import java.io.Serializable;
import java.util.StringTokenizer;

import rpiwcl.cos.test.salsa.mapreduce.Context;
import rpiwcl.cos.test.salsa.mapreduce.Mapper;
import rpiwcl.cos.test.salsa.mapreduce.DoubleIntPair;


public class MeanTempMapper implements Mapper, Serializable {

    public void map( String text, Context context ) {

        if (text.startsWith( "STN" ))
            return;

        String[] tokens = text.split( " +" );
        String yearmon = tokens[1].substring(0, 6);
        double temp = Double.parseDouble( tokens[2] );
        context.write( yearmon, new DoubleIntPair( temp, 1 ) );
    }

}
