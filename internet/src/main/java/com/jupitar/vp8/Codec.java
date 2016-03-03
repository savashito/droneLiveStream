package com.jupitar.vp8;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by rodrigosavage on 3/1/16.
 */
public class Codec {
     String TAG = "CodecInterface";


    String mMIME;
    String mCodecName;
    long DEFAULT_TIMEOUT_US;
    ByteBuffer[] mInputBuffers,mOutputBuffers;
    int mFrameRate,mFrameWidth,mFrameHeight,mFrameIndex;
    MediaFormat mFormat;
    private MediaCodec.BufferInfo mBufferInfo;

    MediaCodec mEncoder;
    protected int mCodecConfiguration;

    public Codec(String codecName, String MIME,int codecConfiguration) {
        mMIME = MIME; // = "video/avc"
        mCodecName = codecName; //  = "OMX.qcom.video.encoder.avc"
        mFrameRate = 30;
        DEFAULT_TIMEOUT_US = 5000;
        mFrameWidth = 0;
        mFrameHeight = 0;
        mFrameIndex = 0;
        mBufferInfo  = new MediaCodec.BufferInfo();
        mCodecConfiguration = codecConfiguration;
        mEncoder = null;
    }
    // 1 146 880
    // 1 382 400
    // 720 w 1280
    public void useDefaultFormat(){
        mFormat = MediaFormat.createVideoFormat(mMIME, mFrameWidth, mFrameHeight);
        mFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        mFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2);
        mFormat.setInteger(MediaFormat.KEY_BIT_RATE, 3500);
        mFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);
        mFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
    }
    public void initCodec(){
        // listCodecs();
        useDefaultFormat();
        Log.d(TAG, "Initializing codec");
        try {
            mEncoder = MediaCodec.createByCodecName(mCodecName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mEncoder.configure(mFormat,
                null,  // surface
                null,  // crypto
                mCodecConfiguration);

        mEncoder.start();
        mInputBuffers = mEncoder.getInputBuffers();
        mOutputBuffers = mEncoder.getOutputBuffers();
    }
    public void stop(){
        if(mEncoder!=null)
            mEncoder.stop();
    }
    public static void listCodecs() {
        int numCodecs = MediaCodecList.getCodecCount();

        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder()) {
                continue;
            }
            Log.d("codec", codecInfo.getName());

            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                Log.d("\tmimes-> ",types[j]);
                MediaCodecInfo.CodecCapabilities capabilitiesForType = codecInfo.getCapabilitiesForType(types[j]);
                int[] colors = capabilitiesForType.colorFormats;
                // capabilitiesForType.
                for (int k = 0; k < colors.length; k++) {
                    Log.d("\t\tcolors-> ","Color: "+colors[k]);

                }
            }
        }
    }


    public void pushFrameToCoder(byte[] frame) {
        Log.d(TAG,"Coding with h "+mFrameHeight +" w "+mFrameWidth );
        int inputBufIndex = mEncoder.dequeueInputBuffer(DEFAULT_TIMEOUT_US);
        if (inputBufIndex >= 0) {
            Log.d(TAG,String.format("inputBufIndex %d, len %d frameSize %d", inputBufIndex,mInputBuffers.length,frame.length));

            mInputBuffers[inputBufIndex].clear();
            mInputBuffers[inputBufIndex].put(frame);
            mInputBuffers[inputBufIndex].rewind();
            int presentationTimeUs = (mFrameIndex * 1000000) / mFrameRate;
            Log.d(TAG, "Encoding frame at index " + mFrameIndex);
            mEncoder.queueInputBuffer(
                    inputBufIndex,
                    0,  // offset
                    frame.length,  // size
                    presentationTimeUs,
                    0);

            mFrameIndex++;
        }
    }

    public byte[] popCodedFrame() {
        int result=-1;
        try {
            result = mEncoder.dequeueOutputBuffer(mBufferInfo, DEFAULT_TIMEOUT_US);
        }catch (Exception e){
            Log.d(TAG,e.getLocalizedMessage());
            return null;
        }
            byte[] buffer = new byte[mBufferInfo.size];
        if (result >= 0) {
            int outputBufIndex = result;
            mOutputBuffers[outputBufIndex].rewind();
            mOutputBuffers[outputBufIndex].get(buffer, 0, mBufferInfo.size);
            /*
            if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                sawOutputEOS = true;
            } else {
                ivf.writeFrame(buffer, mBufferInfo.presentationTimeUs);
            }*/
            mEncoder.releaseOutputBuffer(outputBufIndex, false);

        } else if (result == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
            mOutputBuffers = mEncoder.getOutputBuffers();
        }
        return buffer;
    }

    public void setFrameSize(int width, int height) {
        // is the frameWidth different ?
        if(mFrameWidth != width &&  height != mFrameHeight) {
            mFrameWidth = width;
            mFrameHeight = height;
            Log.d(TAG,"frame h "+height +" w "+width );
            initCodec();
        }
    }
}
