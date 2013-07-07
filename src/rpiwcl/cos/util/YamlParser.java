package rpiwcl.cos.util;

import java.io.File;
import java.io.FileNotFoundException;
import org.ho.yaml.Yaml;

public class YamlParser {
    public Object parse(String file) {
        Object obj = null;

        try {
            obj = Yaml.load(new File(file));
            // System.out.println( object );
        } catch (FileNotFoundException ex) {
            System.err.println( ex );
        }

        return obj;
    }
}
