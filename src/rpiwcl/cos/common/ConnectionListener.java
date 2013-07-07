package rpiwcl.cos.common;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import rpiwcl.cos.common.Message;
import rpiwcl.cos.common.MessageFactory;
import rpiwcl.cos.util.CommChannel;

public class ConnectionListener implements Runnable
{
    ServerSocket listen;
    LinkedBlockingQueue<Message> mailbox;
    public ConnectionListener(int port, LinkedBlockingQueue<Message> queue)
    {
        try{
            listen = new ServerSocket(port);
        } catch(IOException e) {
            e.printStackTrace();
        }
        mailbox = queue;
    }

    private void tellController(CommChannel incoming){
        Message newConnection = MessageFactory.newConnection(incoming);
        try{
            mailbox.put(newConnection);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    public void run()
    {
        int i = 0;
        while(true)
        {
            try{
                CommChannel incoming = new CommChannel(listen.accept());
                tellController(incoming);
System.out.println( "incoming=" + incoming );
                ConnectionHandler connection = new ConnectionHandler(incoming, mailbox);
                new Thread(connection, "ConnectionHandler " + i).start();
            } catch(IOException e) {
                e.printStackTrace();
            }

        }
    }
}
