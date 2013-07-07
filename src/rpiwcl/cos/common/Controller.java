package common;

import java.io.*;
import java.lang.Thread;
import java.net.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import util.CommChannel;

public abstract class Controller
{
    private ServerSocket listener;

    protected LinkedList<CommChannel> children;
    protected LinkedBlockingQueue<Message> mailbox;
    protected MessageFactory msgFactory;


    abstract public void handleMessage(Message msg);

    public Controller(int port){
        mailbox = new LinkedBlockingQueue<Message>();
        children = new LinkedList<CommChannel>();
        ConnectionListener listenLoop = new ConnectionListener(port, mailbox);
        new Thread(listenLoop, "Socket Listener").start();
    }

    public void checkMessages(){
        Message msg;
        while(true){
            try{
                msg = mailbox.take();
                handleMessage(msg);
            } catch(InterruptedException e) {
                //Don't thhink this needs to be worried about;
                e.printStackTrace();
            }
        }
    }

    protected void broadcast(Message msg){
        for( CommChannel s: children ){
            s.write(msg);
        }

    }
}
