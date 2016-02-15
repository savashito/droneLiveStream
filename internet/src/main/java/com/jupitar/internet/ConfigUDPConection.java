package com.jupitar.internet;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by rodrigosavage on 2/5/16.
 */
public class ConfigUDPConection {
    private InetAddress mInetAddress;
    private int mPort;
    //private  String mData;
/*
    public String getData() {
        return mData;
    }
*/
    public ConfigUDPConection() {
        mPort = 9876;
        try {
            mInetAddress = InetAddress.getByName("192.168.0.236");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
//        mData = data;
    }

    public InetAddress getInetAddress() {
        return mInetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        mInetAddress = inetAddress;
    }

    public int getPort() {
        return mPort;
    }

    public void setPort(int port) {
        mPort = port;
    }
}
