package com.jupitar.vp8;

import android.util.Log;

import com.jupitar.internet.ConfigUDPConection;
import com.jupitar.internet.VideoStreamConection;

/**
 * Created by rodrigosavage on 2/29/16.
 */
public class VP8WebEncoder extends Thread{
    private static final String TAG = "VP8WebEncoder";
    boolean mThreadRunning;
    int mFramesToEncode;
    AVCEncoder mVP8Encoder;
    AVCDecoder mVP8Decoder;
    VideoStreamConection mVideoStreamConection;
    // byte[] mFrameData;
    public VP8WebEncoder(ConfigUDPConection config) {
        mFramesToEncode = 0;
        mVP8Encoder = new AVCEncoder();
        mVP8Decoder = new AVCDecoder();
        mThreadRunning = true;
        mVideoStreamConection = new VideoStreamConection(config);
    }

    @Override
    public void run() {
        super.run();
        while (mThreadRunning) {

            if (mFramesToEncode>0) {
                Log.d(TAG, "Sending New frame");
                // see if the encoder has encoded a new frame
                byte[] encodedFrame = mVP8Encoder.popCodedFrame();
                mVP8Decoder.pushFrameToCoder(encodedFrame);
                Log.d(TAG,String.format("Frame encoded Complete %d "+ mFramesToEncode,encodedFrame.length));
                // Frame has been encoded and sent!
                mVideoStreamConection.send(encodedFrame);

                mFramesToEncode--;//  = false;
            }
        }
    }

    public byte[] popDecodedFrame(){
        return mVP8Decoder.popCodedFrame();
    }

    public void close(){
        mVideoStreamConection.close();
//        try {
//        mRunning = false;

    }

    public void debugArray(byte[] arr){
        char[] sArr = new char[100];
        for (int i=0;i<100;i++)
            sArr[i] = (char)arr[i];
        Log.d(TAG,  new String(sArr));
    }
    public void send(byte[] imageInBytes) {

//        debugArray(imageInBytes);
        mVP8Encoder.pushFrameToCoder(imageInBytes);
        // this frame should be enqueue in the correct sequence
      //  mFrameData = imageInBytes;
        Log.d(TAG,"Frame encoded Queue "+ mFramesToEncode);

        mFramesToEncode++;// = true;
    }

    public void setFrameSize(int width, int height) {
      //  m
        mVP8Encoder.setFrameSize(width,height);
        mVP8Decoder.setFrameSize(width,height);
    }
}
