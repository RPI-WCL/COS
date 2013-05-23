package vmManager;

import java.util.*;

import common.MachInfo;
import common.Constants;
import util.CommChannel;

public class VmInfo extends MachInfo
{
    public VmInfo(String address, CommChannel contact) {
        super(address, contact);
    }

    public static LinkedList<String> generateTheaters(Collection<VmInfo> vms) {
        LinkedList<String> theaters = new LinkedList<String>();

        for(VmInfo vm : vms) {
            String addr = vm.getAddress();
            String stripped = addr.substring(addr.indexOf('/')+1, addr.indexOf(':'));
            String result = "rmsp://" + stripped + ":4040/";
            theaters.add(result);
        }

        return theaters;
    }

    public static boolean canAdapt(Collection<VmInfo> vms){
        boolean highCpu = false;
        boolean lowCpu = false;

        for(VmInfo vm: vms){
            if(vm.getCpu() >= Constants.HIGH_CPU){
                highCpu = true;
            } else if ( vm.getCpu() <= Constants.LOW_CPU){
                lowCpu = true;
            }
            
            if(highCpu == true && lowCpu == true){
                return false;
            }
        }
        return true;
    }

    public static double magnitude(Collection<VmInfo> vms){
        double total = 0;
        for(VmInfo vm: vms){
            total += Math.abs(vm.getCpu() - Constants.IDEAL_CPU);
        }
        return total;
    }

    public static double signedSum(Collection<VmInfo> vms){
        double total = 0;
        for(VmInfo vm: vms){
            total += vm.getCpu() - Constants.IDEAL_CPU;
        }
        return total;
    }

    public static double predictCreate(Collection<VmInfo> vms){
        double total = 0;
        for(VmInfo vm: vms){
            double cpu = vm.getCpu();
            System.out.println("CPU in create: " + Double.toString(cpu));
            if(cpu > Constants.IDEAL_CPU){
                total += Math.abs((cpu * (1 - 1.0/vms.size()) - Constants.IDEAL_CPU));
            } else{
                total +=  Math.abs(cpu - Constants.IDEAL_CPU);
            }
        }
        return total;
    }
    public static double predictDestroy(Collection<VmInfo> vms){
        double total = 0;
        for(VmInfo vm: vms){
            double cpu = vm.getCpu();
            if(cpu < Constants.IDEAL_CPU){
                total += Math.abs((cpu * (1 - 1.0/vms.size()) - Constants.IDEAL_CPU));
            } else{
                total +=  Math.abs(cpu - Constants.IDEAL_CPU);
            }
        }
        return total;
    }

}
