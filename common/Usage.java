package common;

import java.io.Serializable;
import java.util.Collection;

import common.Constants;
import util.Utility;

public class Usage implements Serializable {

    static final long serialVersionUID = 42L;

    public double cpu_load;
    public double max_memory; //Note: Max memory available to the JVM, not necessarily the amount of ram on the machine.
    public double used_memory;

    public Usage() {
        cpu_load = Utility.getWeightedSystemLoadAverage();
        max_memory = Utility.getMaxMemory();
        used_memory = Utility.getUsedMemory();
    }

    public Usage(Collection<Usage> history) {

        for(Usage u: history) {
            cpu_load += u.cpu_load;
            max_memory = u.max_memory;
            used_memory += u.used_memory;
        }

        if(cpu_load != 0) {
            cpu_load /= history.size();
        }
        if(used_memory != 0) {
            used_memory /= history.size();
        }
        
    }

    public boolean isExtremeUsage() {
        if(cpu_load < Constants.LOW_CPU || cpu_load > Constants.HIGH_CPU) {
            return true;
        }
        return false;
    }

}
