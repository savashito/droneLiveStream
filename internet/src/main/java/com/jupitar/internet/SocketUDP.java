package com.jupitar.internet;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


/**
 * Created by rodrigosavage on 2/5/16.
 */
public class SocketUDP extends AsyncTask<ConfigUDPConection, Void, Void> {

    DatagramSocket mSocket;
    String mData;
    ConfigUDPConection mConfig;
    DatagramPacket mPacket;
    boolean mRunning,mMessage;
    byte[]mbData;
    @Override
    protected Void doInBackground(ConfigUDPConection... configs) {
        // byte[] buf = new byte[256];
        mConfig = configs[0];
        mRunning = true;
        try {
            //new byte[256];
            byte[] sendBuf = "fd".getBytes();
            mSocket = new DatagramSocket();
            mPacket = new DatagramPacket(sendBuf, sendBuf.length,
                    mConfig.getInetAddress(), mConfig.getPort());

            Log.e("ServerConection","send packet "+ mPacket.getData());
        }  catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("ServerConection", "setupSocker");
        while(mRunning) {
            if(mMessage){

                mMessage = false;
                //byte[] sendBuf = getByteArray();
                //Log.e("fd", "mData mMessage Send : " + sendBuf.length);
                //mPacket.setLength(sendBuf.length);
                //mPacket.setData(sendBuf);
                try {
                    mSocket.send(mPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            /*
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
        // Thread.sleep(50);
        return null;
    }

    private byte[] getByteArray() {

        if (mData != null) {
            return mData.getBytes();
        } else {
            return mbData;

        }

    }
    public void send(String sData){
//        mData = data;

        if(mPacket!=null) {
            mMessage = true;
            byte[] data = sData.getBytes();
            mPacket.setData(data);
            mPacket.setLength(data.length);
            // Log.e("fd","send() "+data);
        }

    }
    public void send(byte[] data){
//        mData = null;
//        mbData = data;
        if(mPacket!=null) {

            mPacket.setData(data);

            mPacket.setLength(1280*45); // 1280 * 720 * 3
            mMessage = true;
        }
    }
    void exit(){
        mRunning = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.e("fd","Salio");
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
