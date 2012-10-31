package util;

import java.lang.StringBuilder;
import java.util.List;
import java.util.ArrayList;
import java.net.*;

public class Messages
{
    private static String get_host_address_string()
    {
        InetAddress host;
        try
        {
            host = InetAddress.getLocalHost();
        }
        catch(UnknownHostException e)
        {
            return null;
        }
        return host.getHostAddress();
    }

    private static boolean add_sender(StringBuilder builder )
    {
        String return_addr = get_host_address_string();
        if( return_addr == null ) return false;
        addStringAndSpace(builder, return_addr);
        return true;
    }

    private static void addStringAndSpace(StringBuilder builder, String msg)
    {
        builder.append(msg);
        builder.append(" ");
    }

    public static String create_vm_request(String vmCfgFile, Iterable<String> peerTheaters)
    {
        StringBuilder msg = new StringBuilder("create_vm_request ");
        if(!add_sender(msg)) return null;
        addStringAndSpace(msg, vmCfgFile);
        for( String s: peerTheaters )
            addStringAndSpace(msg, s);
        return msg.toString().trim();
    }

    public static String create_vm_response(String result, String address)
    {
        StringBuilder msg = new StringBuilder("create_vm_response ");
        if(!add_sender(msg)) return null;
        addStringAndSpace(msg, result);
        if( address != null) addStringAndSpace(msg, address);
        return msg.toString().trim();
    }

    public static String destroy_vm_request(String vmName)
    {
        StringBuilder msg = new StringBuilder("destroy_vm_request ");
        if(!add_sender(msg)) return null;
        addStringAndSpace(msg, vmName);
        return msg.toString().trim();
    }

    public static String destroy_vm_response(String result, String vmName)
    {
        StringBuilder msg = new StringBuilder("destroy_vm_response ");
        if(!add_sender(msg)) return null;
        addStringAndSpace(msg, result);
        addStringAndSpace(msg, vmName);
        return msg.toString().trim();
    }

    public static List<String> get_params(String msg)
    {
        if( msg == null)
            return null;
        String[] params= msg.split(" ");
        //Don't bother specifying an initial capacity. Defaults to 10. Plenty.
        ArrayList<String> payload = new ArrayList<String>();
        for( int i = 2; i < params.length; i++)
        {
            payload.add(params[i]);
        }
        return payload;
    }

    public static String get_request_type(String msg)
    {
        if( msg == null)
            return null;
        //Splits the string around space.
        //First slot is always message type.
        return msg.split(" ")[0];
    }

    public static String get_return_addr(String msg)
    {
        if( msg == null)
            return null;
        //Splits the string around space.
        //Second slot is always return address.
        return msg.split(" ")[1];
    }

    public static String notify_high_cpu_usage(double load)
    {
        StringBuilder msg = new StringBuilder("notify_high_cpu_usage ");
        if(!add_sender(msg)) return null;
        msg.append(Double.toString(load));
        return msg.toString().trim();
    }

    public static String notify_low_cpu_usage(double load)
    {
        StringBuilder msg = new StringBuilder("notify_low_cpu_usage ");
        if(!add_sender(msg)) return null;
        msg.append(Double.toString(load));
        return msg.toString().trim();
    }

    public static String notify_cpu_usage(double load)
    {
        StringBuilder msg = new StringBuilder("notify_cpu_usage ");
        if(!add_sender(msg)) return null;
        msg.append(Double.toString(load));
        return msg.toString().trim();
    }

    public static String notify_vm_started(String vmMonAddr, String theater)
    {
        StringBuilder msg = new StringBuilder("notify_vm_started ");
        if(!add_sender(msg)) return null;
        addStringAndSpace(msg, vmMonAddr);
        addStringAndSpace(msg, theater);
        return msg.toString().trim();
    }

    public static String shutdown_theater_request(String vmMonAddr, String theater)
    {
        StringBuilder msg = new StringBuilder("shutdown_theater_request ");
        if(!add_sender(msg)) return null;
        addStringAndSpace(msg, vmMonAddr);
        addStringAndSpace(msg, theater);
        return msg.toString().trim();
    }

    public static String shutdown_theater_response(String result, String vmMonAddr)
    {
        StringBuilder msg = new StringBuilder("shutdown_theater_response ");
        if(!add_sender(msg)) return null;
        addStringAndSpace(msg, result);
        addStringAndSpace(msg, vmMonAddr);
        return msg.toString().trim();
    }
}
