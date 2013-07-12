package rpiwcl.cos.common;

import java.io.*;
import java.lang.Thread;
import java.net.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import rpiwcl.cos.common.ConnectionListener;
import rpiwcl.cos.common.Message;
import rpiwcl.cos.common.MessageFactory;
import rpiwcl.cos.util.CommChannel;


public abstract class Controller {
    protected static final int STATE_NULL = 0;
    protected static final int STATE_INITIALIZING = 1;
    protected static final int STATE_READY = 2;

    private ServerSocket listener;

    protected LinkedList<CommChannel> children;
    protected LinkedBlockingQueue<Message> mailbox;
    protected MessageFactory msgFactory;
    protected String id;
    protected int state;

    abstract public void handleMessage(Message msg);

    public Controller(int port) {
        this.mailbox = new LinkedBlockingQueue<Message>();
        this.children = new LinkedList<CommChannel>();
        this.id = null;
        this.state = STATE_NULL;
        ConnectionListener listenLoop = new ConnectionListener(port, mailbox);
        new Thread(listenLoop, "Socket Listener").start();
    }

    public Controller(int port, String id) {
        this.mailbox = new LinkedBlockingQueue<Message>();
        this.children = new LinkedList<CommChannel>();
        this.id = id;
        ConnectionListener listenLoop = new ConnectionListener(port, mailbox);
        new Thread(listenLoop, "Socket Listener").start();
    }

    public void checkMessages() {
        Message msg;
        while(true) {
            try{
                msg = mailbox.take();
                if (msg.getMethod().equals( "shutdown" )) {
                    break;
                }
                handleMessage(msg);
            } catch(InterruptedException e) {
                //Don't thhink this needs to be worried about;
                e.printStackTrace();
            }
        }
    }

    protected void broadcast(Message msg) {
        for(CommChannel s: children) {
            s.write(msg);
        }
    }

    protected void dbgPrint( String str ) {
        System.out.println( "[" + id + "] " + str );
    }

    protected void errPrint( String str ) {
        System.err.println( "[" + id + "] " + str );
    }
}
