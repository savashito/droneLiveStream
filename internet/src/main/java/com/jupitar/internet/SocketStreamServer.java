package com.jupitar.internet;


import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketStreamServer extends Thread{
    public static String tag = "Server";
    public SocketStreamServer(){
        mPort = 8080;
        try {
            mServerSocket = new ServerSocket(mPort);
        } catch (IOException ex) {
            Log.e(tag, "Can't setup server on port number: " + mPort);

        }
    }
    public void run(){
        Socket mSocket;
        InputStream in = null;
        OutputStream out = null;
        int size = 16*1024;
        byte[] bytes = new byte[size];
        byte[] outImage = null;

        try {
            mSocket = mServerSocket.accept();
            in = mSocket.getInputStream();
            ByteArrayOutputStream bOutput = new ByteArrayOutputStream(size*12);
//            out = new FileOutputStream("M:\\test2.xml");
            int count;
            while ((count = in.read(bytes)) > 0) {
                bOutput.write(bytes, 0, count);
            }
            outImage = bOutput.toByteArray();
            Log.d(tag,"Got message "+ outImage[0]+outImage[1]+outImage[2]);
            bOutput.close();
            in.close();
            mSocket.close();
            mServerSocket.close();
        } catch (FileNotFoundException ex) {
            System.out.println("File not found. ");
        } catch (IOException ex) {
            Log.e(tag,"Can't accept client connection. "+ex.getLocalizedMessage());
        }
        // return outImage;

    }

    ServerSocket mServerSocket;
    int mPort;


}