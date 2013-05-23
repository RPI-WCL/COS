package util;

import java.lang.management.*;

import common.Constants;

public class Utility
{
    private final static OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
    private final static int cpuCount = os.getAvailableProcessors();

    public static void debugPrint(String msg){
        if(Constants.DEBUG)
            System.out.println(msg);
    }

    public static double getSystemLoadAverage(){
        return os.getSystemLoadAverage();    
    }

    public static double getWeightedSystemLoadAverage(){
       return os.getSystemLoadAverage() / cpuCount; 
    }
}
