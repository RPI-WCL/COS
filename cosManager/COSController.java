package cosManager;

import java.io.*;
import java.lang.System;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

import common.Controller;
import util.CommChannel;

public class COSController extends Controller
{
    public COSController()
    {
        super(9999);
    }

    public void periodic()
    {
    }

    public void handleMessage( String message, CommChannel sock)
    {
        System.out.println(message);
    }

    public static void main(String[] args) throws Exception
    {
        COSController runner = new COSController();
        runner.checkMessages();
    }
}

