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
    String remoteAddr;
    public ConnectionHandler(Socket sock, LinkedBlockingQueue<Message> queue) {
        channel = new CommChannel(sock);
        mailbox = queue;
        remoteAddr = sock.getRemoteSocketAddress().toString();
    }

    public ConnectionHandler(CommChannel chan, LinkedBlockingQueue<Message> queue) {
        channel = chan;
        mailbox = queue;
        remoteAddr = chan.getRemoteAddr();
    }

    public void run(){
        Message msg;
        Object rcved;
        int count = 0;
        while(true) {
            rcved = channel.read();
            if(rcved instanceof Message) {
                count = 0;
                msg = (Message) rcved;
                msg.setReply(channel);
                try{
                    mailbox.put(msg);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            else{
                count++;
            }

            if(count == 16) {
                //Since the connection is most likely broken at this point
                //a flag should probably be set.
                msg = MessageFactory.droppedConnection(remoteAddr);
                try{
                    mailbox.put(msg);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                return;
            }

            msg = null;
        }
    }
}
