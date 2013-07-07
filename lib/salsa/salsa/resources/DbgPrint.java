package salsa.resources;

public class DbgPrint {
    public static int NULL     = 0x00;
    public static int DEBUG    = 0x01;
    public static int INFO     = 0x02;
    public static int WARN     = 0x04;
    public static int ERROR    = 0x08;
    public static int ALL      = 0xFF;

    private static boolean on_ = false;
    private static boolean init_ = false;
    private static int mask_ = NULL;

    public static void print( int level, String module, String text ) {
        if (!init_) {
            String debug = System.getProperty( "dbg" );
            if (debug != null && debug.compareToIgnoreCase( "on" ) == 0) {
                on_ = true;
                mask_ = ALL;
            }
            else if (debug != null) {
                on_ = true;
                try {
                    mask_ = Integer.parseInt( debug, 16 );
                } catch (Exception ex) {
                    System.err.println( "Debug print mask level parse error: " + debug );
                    mask_ = NULL;
                    on_ = false;
                }
            }
            init_ = true;
        }
        
        if (on_ && ((level & mask_) != 0))
            System.out.println( "[" + module + "] " + text );
    }

    public static void print( String text ) {
        if (!init_) {
            String debug = System.getProperty( "dbg" );
            if (debug != null && debug.compareToIgnoreCase( "on" ) == 0) {
                on_ = true;
                mask_ = ALL;
            }
            init_ = true;
        }
        
        if (on_)
            System.out.println( "[null] " + text );
    }

    public static void main( String argv[] ) {
        int a = 5, b = 3;
        DbgPrint.print( DbgPrint.DEBUG, "DbgPrint", "a = " + a );
        DbgPrint.print( DbgPrint.INFO, "DbgPrint", "a*b = " + (a * b) );
    }
}
