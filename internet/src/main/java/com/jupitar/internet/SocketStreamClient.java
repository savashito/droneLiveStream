package com.jupitar.internet;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class SocketStreamClient extends Thread{
    public static String tag = "Client";
    Socket mSocket;
    InetAddress mHost;
    int mPort,mSize;
    boolean mRunning,mMessage;
    byte[] mBuffer,mData;

    public SocketStreamClient(ConfigUDPConection config) {
        mPort = config.getPort();
        mHost = config.getInetAddress();
        mSize = 16 * 1024;
    }

    public void close(){
//        try {
            mRunning = false;

    }

    byte[] testBuffer(){
        int n = mSize*12;
        byte[] bigTestBuffer = new byte[n];
        for (int i=0;i<n;i++){
            bigTestBuffer[i] = (byte)(i%255);
        }
        return bigTestBuffer;
    }
    public SocketStreamClient(){
        mPort = 8080;
       // mHost = "127.0.0.1";
        mSize = 16 * 1024;
    }
    public void sendTestData(){
        send(testBuffer());
    }
    public void run(){
        mBuffer = new byte[mSize];
        mRunning = true;
        mMessage = false;
        while(mRunning) {
            if(mMessage) {
                mMessage = true;
                sendInternal();
            }

        }
        //sendTestData();
    }
    public  void send(byte[] byteArray) {
        mMessage = true;
        mData = byteArray;
    }
    /*
    public  void send(String text) {
        mMessage = true;
        mData = text.getBytes();
    }*/

    private void sendInternal(){

        ByteArrayInputStream bInput = new ByteArrayInputStream(mData);
        try {
            mSocket = new Socket(mHost, mPort);
            OutputStream out = mSocket.getOutputStream();

            int count;
            while ((count = bInput.read(mBuffer)) > 0) {
                out.write(mBuffer, 0, count);
            }
            out.close();
            bInput.close();
            mSocket.close();
        } catch (IOException ex) {
//            Log.e(tag, "Can't setup server on port number: " + mPort);
            Log.e(tag, "Can't accept client connection. " + ex.getLocalizedMessage());
        }
    }

}