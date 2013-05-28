package util;

import java.lang.management.*;
import java.lang.Runtime;

import common.Constants;

public class Utility
{
    private final static OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
    private final static Runtime runtime= Runtime.getRuntime();
    private final static int cpuCount = os.getAvailableProcessors();

    public static void debugPrint(String msg) {
        if(Constants.DEBUG)
            System.out.println(msg);
    }

    public static double getSystemLoadAverage() {
        return os.getSystemLoadAverage();    
    }

    public static double getWeightedSystemLoadAverage() {
       return os.getSystemLoadAverage() / cpuCount; 
    }

    public static double getMaxMemory() {
        return runtime.maxMemory();
    }

    public static double getUsedMemory() {
        return runtime.totalMemory();
    }
}
