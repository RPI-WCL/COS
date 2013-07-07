package salsa.examples.mapreduce;

import java.util.HashMap;
import java.util.Vector;
import java.io.Serializable;


public class Context implements Serializable {
    HashMap<String, Vector<Object>> map;

    public Context() {
        map = new HashMap<String, Vector<Object>>();
    }
    
    public Context( HashMap map ) {
        this.map = map;
    }

    public void clear() {
        map.clear();
    }

    public void write( String key, Object value ) {
        // System.out.println( "write, key=" + key + ", value=" + value );

        Vector v = map.get( key );
        if (v == null)
            v = new Vector<Object>();

        v.add( value );
        map.put( key, v );
    }

    public HashMap getMap() {
        return map;
    }
}
        
        

    
