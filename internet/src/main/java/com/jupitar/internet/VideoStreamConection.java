package com.jupitar.internet;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by rodrigosavage on 3/2/16.
 */
public class VideoStreamConection {
    public static String TAG = "VideoStreamConection";
    Socket mSocket;
    InetAddress mHost;
    int mPort,mBufferSize;
    byte[] mBuffer;

    public VideoStreamConection(ConfigUDPConection config) {
        mPort = config.getPort();
        mHost = config.getInetAddress();
        mBufferSize = 16 * 1024;
        mBuffer = new byte[mBufferSize];
    }

    public void send(byte[] encodedFrame) {
        // Create input stream from encodeFrame byte array
        Log.d(TAG,"sending encoded frame "+encodedFrame.length);
        if(encodedFrame==null || encodedFrame.length <30 )
            return;
        ByteArrayInputStream bInput = new ByteArrayInputStream(encodedFrame);
        try {
            mSocket = new Socket(mHost, mPort);
            OutputStream out = mSocket.getOutputStream();
            // read mBufferSize each time and send it,
            // sending xometing bigger than mBufferSize is not good
            int count;
            while ((count = bInput.read(mBuffer)) > 0) {

                out.write(mBuffer, 0, count);
            }
//            out.write(mBuffer, 0, count);
            out.close();
            bInput.close();
            mSocket.close();
        } catch (IOException ex) {
//            Log.e(tag, "Can't setup server on port number: " + mPort);
            Log.e(TAG, "Can't accept client connection. " + ex.getLocalizedMessage());
        }
    }

    public void close() {

    }
}
