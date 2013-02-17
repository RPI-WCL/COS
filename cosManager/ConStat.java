package cosManager;

import common.Constants;
import util.CommChannel;
import java.util.*;
import java.lang.*;

public class ConStat
{
    double cpu;
    String ip_addr;
    CommChannel parent;

    public static double magnitude( Map<String, ConStat> map ){
        double total = 0;
        for(Map.Entry<String, ConStat> entry : map.entrySet() ){
           total += Math.abs( entry.getValue().getCur() - Constants.IDEAL_CPU ); 
        }
        return total;
    }

    public static double signedSum(Map<String, ConStat> map ){
        double total = 0;
        for(Map.Entry<String, ConStat> entry : map.entrySet() ){
           total += entry.getValue().getCur() - Constants.IDEAL_CPU; 
        }
        return total;
    }

    public static double predictCreate( Map<String, ConStat> map ){
        double total = 0;
        for(Map.Entry<String, ConStat> entry : map.entrySet() ){
            double cur = entry.getValue().getCur();
            if( cur > Constants.IDEAL_CPU ){
                total += cur * (1 - 1/ map.size()) - Constants.IDEAL_CPU;
            }
            else{
                total += Math.abs( cur - Constants.IDEAL_CPU ); 
            }
        }
        return total;
    }

    public static double predictDestroy( Map<String, ConStat> map ){
        double total = 0;
        for(Map.Entry<String, ConStat> entry : map.entrySet() ){
            double cur = entry.getValue().getCur();
            if( cur < Constants.IDEAL_CPU ){
                total += cur * (1 + 1/ map.size()) - Constants.IDEAL_CPU;
            }
            else{
                total += Math.abs( cur - Constants.IDEAL_CPU ); 
            }
        }
        return total;
    }

    public static boolean canChange( Map<String, ConStat> map){
        boolean high = false;
        boolean low = false;
        for(Map.Entry<String, ConStat> entry : map.entrySet() ){
            double cur = entry.getValue().getCur();
            if( cur > Constants.HIGH_CPU ) high = true;
            if( cur < Constants.LOW_CPU ) low = true;
            if( high && low ) break;
        }
        if( high == true && low == true )
            return false;
        else return true;
    }

    public static String findMinVm( Map<String, ConStat> map){
        double min = 0;
        String minKey = null;
        for(Map.Entry<String, ConStat> entry : map.entrySet()) {
            if( minKey == null || entry.getValue().getCur() < min ) {
                min = entry.getValue().getCur();
                minKey = entry.getKey();
            }
        }
        return minKey;
    }

    public static List<String> get_theaters( Map<String, ConStat> map){
        LinkedList<String> theaters = new LinkedList<String>();
        for( String s: map.keySet() ){
            String stripped = s.substring(s.indexOf('/') + 1, s.indexOf(':'));
            stripped = "rmsp://" + stripped + ":4040/";
            theaters.add(stripped);
        }
        return theaters;
    }

    public static CommChannel findMinNode( Map<CommChannel, ConStat> map){
        double min = 0;
        CommChannel minKey = null;
        for(Map.Entry<CommChannel, ConStat> entry : map.entrySet()){
            if( minKey == null || entry.getValue().getCur() < min ){
                min = entry.getValue().getCur();
                minKey = entry.getKey();
            }
        }
        return minKey;
    }
}
