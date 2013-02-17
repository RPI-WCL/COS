package common;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import common.Message;
import common.MessageFactory;
import util.CommChannel;

public class ConnectionHandler implements Runnable
{
    CommChannel channel;
    LinkedBlockingQueue<Message> mailbox;
    public ConnectionHandler(Socket sock, LinkedBlockingQueue<Message> queue){
        channel = new CommChannel(sock);
        mailbox = queue;
    }

    public ConnectionHandler(CommChannel chan, LinkedBlockingQueue<Message> queue){
        channel = chan;
        mailbox = queue;
    }

    public void run(){
        Message msg;
        Object rcved;
        while(true)
        {
            rcved = channel.read();
            if(rcved instanceof Message)
            {
                msg = (Message) rcved;
                try{
                    mailbox.put(msg);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
