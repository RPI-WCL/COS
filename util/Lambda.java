package util;

import java.lang.management.*;

public class Lambda
{
    private static final Lambda instance = new Lambda();
    private OperatingSystemMXBean os;
    private int cpuCount;

    private Lambda()
    {
        os = ManagementFactory.getOperatingSystemMXBean();
        cpuCount = os.getAvailableProcessors();
    }

    public static Lambda getInstance()
    {
        return instance;
    }

    public double getSystemLoadAverage()
    {
        return os.getSystemLoadAverage();    
    }

    public double getWeightedSystemLoadAverage()
    {
       return os.getSystemLoadAverage() / cpuCount; 
    }
}
