package common;

import java.io.*;
import java.util.*;

import util.CommChannel;

/* Class encapsulating message transmissions
 *
 */
public class Message implements Serializable
{
    static final long serialVersionUID = 42L;
    
    private String method;
    private String sender;
    transient private CommChannel reply;
    private TreeMap<String, Serializable> args;

    public Message(String method, String sender){
        this.method = method;
        this.sender = sender;
        args = new TreeMap<String, Serializable>();
    }

    public String getMethod(){
        return method;
    }

    public String getSender(){
        return sender;
    }

    public void setReply(CommChannel chan){
        reply = chan;
    }

    public CommChannel getReply(){
        return reply;
    }

    public void addParam(String key, Serializable value){
        args.put(key, value);
    }

    public Serializable getParam(String key){
        return args.get(key);
    }

}
