package common;

import util.CommChannel;

public class MessageFactory
{
    String type; //VM, NODE, COS
    String localhost;

    public MessageFactory(String type, CommChannel any)
    {
       this.type = type; 
       this.localhost = any.getLocalAddr();
    }

    public Message notifyCpuUsage(Double load)
    {
       Message payload = new Message( "cpu_usage", localhost);
       payload.addParam("load", load);
       payload.addParam("type", type);
       return payload;

    }
    
    public Message CpuUsgageResp(Double load)
    {
       Message payload = new Message( "cpu_usage_resp", localhost);
       payload.addParam("load", load);
       payload.addParam("type", type);
       return payload;

    }

    public Message getCpuUsage()
    {
        Message payload = new Message( "get_cpu_usage", localhost);
        return payload;
    }

}
