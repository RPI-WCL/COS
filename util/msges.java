public String create_vm_request(String vmCfgFile, Iterable<String> peerTheaters)
{
    StringBuilder msg = new StringBuilder("create_vm_request ");
    addStringAndSpace(msg, localHost);
    addStringAndSpace(msg, vmCfgFile);
    for( String s: peerTheaters )
        addStringAndSpace(msg, s);
    return msg.toString().trim();
}

public String create_vm_response(String result, String address)
{
    StringBuilder msg = new StringBuilder("create_vm_response ");
    addStringAndSpace(msg, localHost);
    addStringAndSpace(msg, result);
    if( address != null) addStringAndSpace(msg, address);
    return msg.toString().trim();
}

public String create_theater(Iterable<String> peerTheaters)
{
    StringBuilder msg = new StringBuilder("create_theater ");
    addStringAndSpace(msg, localHost);
    for( String s: peerTheaters )
        addStringAndSpace(msg, s);
    return msg.toString().trim();
}

public String destroy_vm_request(String vmName)
{
    StringBuilder msg = new StringBuilder("destroy_vm_request ");
    addStringAndSpace(msg, localHost);
    addStringAndSpace(msg, vmName);
    return msg.toString().trim();
}

public String destroy_vm_response(String result, String vmAddr)
{
    StringBuilder msg = new StringBuilder("destroy_vm_response ");
    addStringAndSpace(msg, localHost);
    addStringAndSpace(msg, result);
    addStringAndSpace(msg, vmAddr);
    return msg.toString().trim();
}

public String notify_vm_started(String vmMonAddr, String theater)
{
    StringBuilder msg = new StringBuilder("notify_vm_started ");
    addStringAndSpace(msg, localHost);
    addStringAndSpace(msg, vmMonAddr);
    addStringAndSpace(msg, theater);
    return msg.toString().trim();
}

public String shutdown_request()
{
    StringBuilder msg = new StringBuilder("shutdown_request ");
    addStringAndSpace(msg, localHost);
    return msg.toString().trim();
}

public String shutdown_theater_request(String vmMonAddr, String theater)
{
    StringBuilder msg = new StringBuilder("shutdown_theater_request ");
    addStringAndSpace(msg, localHost);
    addStringAndSpace(msg, vmMonAddr);
    addStringAndSpace(msg, theater);
    return msg.toString().trim();
}

public String shutdown_theater_response(String result, String vmMonAddr)
{
    StringBuilder msg = new StringBuilder("shutdown_theater_response ");
    addStringAndSpace(msg, localHost);
    addStringAndSpace(msg, result);
    addStringAndSpace(msg, vmMonAddr);
    return msg.toString().trim();
}
