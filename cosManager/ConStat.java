package cosManager;

import util.CommChannel;
import java.util.*;

public class ConStat
{
    double cur_cpu;
    double prev_cpu;
    String ip_addr;
    CommChannel parent;

    public ConStat()
    {
        cur_cpu = 0;
        prev_cpu = 0;
    }

    public double getCur()
    {
        return cur_cpu;
    }

    public double getPrev()
    {
        return prev_cpu;
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
        prev_cpu = cur_cpu;
        cur_cpu = val;
    }

    public static double avg(Map<String, ConStat> map)
    {
        double sum = 0; 
        for( ConStat x: map.values())
        {
            sum += x.getCur();
        }
        return sum / map.size();
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
