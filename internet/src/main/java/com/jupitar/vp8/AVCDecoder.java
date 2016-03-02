package com.jupitar.vp8;

import android.media.MediaFormat;
import android.util.Log;

/**
 * Created by rodrigosavage on 3/1/16.
 */

public class AVCDecoder extends Codec{
    @Override
    public void useDefaultFormat() {
        // super.useDefaultFormat();
        mFormat = MediaFormat.createVideoFormat(mMIME, mFrameWidth, mFrameHeight);
        Log.d("miau", "useDefault Decoder");
    }

    public AVCDecoder() {
        super("OMX.qcom.video.decoder.avc", "video/avc", 0);
    }
}

/*
public class VP8Decoder {
    private static final String MIME = "video/avc";
    private static final String TAG = "VP8Decoder";
    private static final String DECODER_NAME = "OMX.qcom.video.decoder.avc";
    MediaCodec mEncoder;
    int mWidth,mHeight;
    private void initDecoder(){

        MediaFormat format = MediaFormat.createVideoFormat(MIME,
                mWidth,
                mHeight);
        Log.d(TAG, "Creating decoder");
        MediaCodec decoder = null;
        try {
            decoder = MediaCodec.createByCodecName(DECODER_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
        decoder.configure(format,
                null,  // surface
                null,  // crypto
                0);  // flags
        decoder.start();
    }
    public byte[] popDecodedFrame() {

    }

    public void pushFrameToDecode(byte[] encodedFrame) {


    }
    public void setFrameSize(int width, int height) {
        // is the frameWidth different ?
        if(mWidth != width &&  height != mHeight) {
            mWidth = width;
            mHeight = height;
            Log.d(TAG,"frame h "+height +" w "+width );
            initDecoder();
        }
    }
}
*/