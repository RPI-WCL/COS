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

    public static String create_vm_request()
    {
        StringBuilder msg = new StringBuilder("create_vm_request ");
        String return_addr = get_host_address_string();
        if( return_addr == null )
            return null;
        msg.append(return_addr);
        msg.append(" ");
        return msg.toString();
    }

    public static String create_vm_response()
    {
        StringBuilder msg = new StringBuilder("create_vm_response ");
        String return_addr = get_host_address_string();
        if( return_addr == null )
            return null;
        msg.append(return_addr);
        msg.append(" ");
        return msg.toString();
    }

    public static String destroy_vm_request()
    {
        StringBuilder msg = new StringBuilder("destroy_vm_request ");
        String return_addr = get_host_address_string();
        if( return_addr == null )
            return null;
        msg.append(return_addr);
        msg.append(" ");
        return msg.toString();
    }

    public static String destroy_vm_response()
    {
        StringBuilder msg = new StringBuilder("destroy_vm_response ");
        String return_addr = get_host_address_string();
        if( return_addr == null )
            return null;
        msg.append(return_addr);
        msg.append(" ");
        return msg.toString();
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
        String return_addr = get_host_address_string();
        if( return_addr == null )
            return null;
        msg.append(return_addr);
        msg.append(" ");
        msg.append(Double.toString(load));
        return msg.toString();
    }

    public static String notify_low_cpu_usage()
    {
        StringBuilder msg = new StringBuilder("notify_low_cpu_usage ");
        String return_addr = get_host_address_string();
        if( return_addr == null )
            return null;
        msg.append(return_addr);
        msg.append(" ");
        return msg.toString();
    }

    public static String notify_vm_started()
    {
        StringBuilder msg = new StringBuilder("notify_vm_started ");
        String return_addr = get_host_address_string();
        if( return_addr == null )
            return null;
        msg.append(return_addr);
        msg.append(" ");
        return msg.toString();
    }

    public static String shutdown_theater_request()
    {
        StringBuilder msg = new StringBuilder("shutdown_theater_request ");
        String return_addr = get_host_address_string();
        if( return_addr == null )
            return null;
        msg.append(return_addr);
        msg.append(" ");
        return msg.toString();
    }

    public static String shutdown_theater_response()
    {
        StringBuilder msg = new StringBuilder("shutdown_theater_response ");
        String return_addr = get_host_address_string();
        if( return_addr == null )
            return null;
        msg.append(return_addr);
        msg.append(" ");
        return msg.toString();
    }
}
