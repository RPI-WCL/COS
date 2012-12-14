package cosManager;

import util.CommChannel;
import java.util.*;
import java.lang.*;

public class ConStat
{

    static final double lower =.25;
    static final double upper =.75;
    static final double ideal =.50;
    double cpu;
    String ip_addr;
    CommChannel parent;

    public ConStat()
    {
        cpu = 0;
    }

    public double getCur()
    {
        return cpu;
    }

    public void setParent(CommChannel par)
    {
        parent = par;
    }

    public CommChannel getParent()
    {
        return parent;
    }

    public void setIpAddr(String ip)
    {
        ip_addr = ip;
    }

    public String getIpAddr()
    {
        return ip_addr;
    }

    public void update(double val)
    {
        cpu = val;
    }

    public static double magnitude( Map<String, ConStat> map )
    {
        double total = 0;
        for(Map.Entry<String, ConStat> entry : map.entrySet() )
        {
           total += Math.abs( entry.getValue().getCur() - ideal ); 
        }
        return total;
    }

    public static double signedSum(Map<String, ConStat> map )
    {
        double total = 0;
        for(Map.Entry<String, ConStat> entry : map.entrySet() )
        {
           total += entry.getValue().getCur() - ideal; 
        }
        return total;
    }

    public static double predictCreate( Map<String, ConStat> map )
    {
        double total = 0;
        for(Map.Entry<String, ConStat> entry : map.entrySet() )
        {
            double cur = entry.getValue().getCur();
            if( cur > ideal )
            {
                total += cur * (1 - 1/ map.size()) - ideal;
            }
            else
            {
                total += Math.abs( cur - ideal ); 
            }
        }
        return total;
    }

    public static double predictDestroy( Map<String, ConStat> map )
    {
        double total = 0;
        for(Map.Entry<String, ConStat> entry : map.entrySet() )
        {
            double cur = entry.getValue().getCur();
            if( cur < ideal )
            {
                total += cur * (1 + 1/ map.size()) - ideal;
            }
            else
            {
                total += Math.abs( cur - ideal ); 
            }
        }
        return total;
    }

    public static boolean canChange( Map<String, ConStat> map)
    {
        boolean high = false;
        boolean low = false;
        for(Map.Entry<String, ConStat> entry : map.entrySet() )
        {
            double cur = entry.getValue().getCur();
            if( cur > upper )
                high = true;
            if( cur < lower )
                low = true;
            if( high && low )
                break;
        }
        if( high == true && low == true )
            return false;
        else return true;
    }

    public static String findMinVm( Map<String, ConStat> map)
    {
        double min = 0;
        String minKey = null;
        for(Map.Entry<String, ConStat> entry : map.entrySet())
        {
            if( minKey == null || entry.getValue().getCur() < min )
            {
                min = entry.getValue().getCur();
                minKey = entry.getKey();
            }
        }
        return minKey;
    }

    public static List<String> get_theaters( Map<String, ConStat> map)
    {
        LinkedList<String> theaters = new LinkedList<String>();
        for( String s: map.keySet() )
        {
            String stripped = s.substring(s.indexOf('/') + 1, s.indexOf(':'));
            stripped = "rmsp://" + stripped + ":4040/";
            theaters.add(stripped);
        }
        return theaters;
    }

    public static CommChannel findMinNode( Map<CommChannel, ConStat> map)
    {
        double min = 0;
        CommChannel minKey = null;
        for(Map.Entry<CommChannel, ConStat> entry : map.entrySet())
        {
            if( minKey == null || entry.getValue().getCur() < min )
            {
                min = entry.getValue().getCur();
                minKey = entry.getKey();
            }
        }
        return minKey;
    }


}
