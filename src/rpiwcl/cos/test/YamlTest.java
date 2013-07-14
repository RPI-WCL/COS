package rpiwcl.cos.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import org.ho.yaml.Yaml;


public class YamlTest {
    public static void main(String[] args) {
        try {
            HashMap object = (HashMap)Yaml.load(new File(args[0]));
            System.out.println( object );
            HashMap cos = (HashMap)object.get( "cos" );
            System.out.println( Yaml.dump( cos ) );
        } catch (FileNotFoundException ex) {
            System.err.println( ex );
        }
    }
}
