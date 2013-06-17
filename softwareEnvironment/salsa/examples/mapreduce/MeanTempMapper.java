package salsa.examples.mapreduce;

import java.io.Serializable;
import java.util.StringTokenizer;

import salsa.examples.mapreduce.Context;
import salsa.examples.mapreduce.Mapper;
import salsa.examples.mapreduce.DoubleIntPair;


public class MeanTempMapper implements Mapper, Serializable {

    public void map( String text, Context context ) {

        if (text.startsWith( "STN" ))
            return;

        String[] tokens = text.split( " +" );
        String yearmon = tokens[2].substring(0, 6);
        double temp = Double.parseDouble( tokens[3] );
        context.write( yearmon, new DoubleIntPair( temp, 1 ) );
    }

}
