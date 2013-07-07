package common;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import common.Message;
import common.MessageFactory;
import util.CommChannel;

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
                ConnectionHandler connection = new ConnectionHandler(incoming, mailbox);
                new Thread(connection, "ConnectionHandler " + i).start();
            } catch(IOException e) {
                e.printStackTrace();
            }

        }
    }
}
