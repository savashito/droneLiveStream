package com.jupitar.vp8;

import android.media.MediaCodec;

/**
 * Created by rodrigosavage on 2/29/16.
 */
public class AVCEncoder extends Codec{
    public AVCEncoder() {
        super("OMX.qcom.video.encoder.avc", "video/avc", MediaCodec.CONFIGURE_FLAG_ENCODE);
    }
}
/*
    public class VP8Encoder2 {

    private static final String TAG = "VP8EncoderTest";

//    private static final String VP8_MIME = "video/x-vnd.on2.vp8";
    //    private static final String VPX_DECODER_NAME = "OMX.google.vpx.decoder";
//    private static final String VPX_ENCODER_NAME = "OMX.google.vpx.encoder";
//    private static final String VP8_MIME = "video/mp4v-es";
//    private static final String VPX_ENCODER_NAME = "OMX.qcom.video.encoder.mpeg4";
    private static final String VP8_MIME = "video/avc";
    private static final String VPX_ENCODER_NAME = "OMX.qcom.video.encoder.avc";
    //    private static final String BASIC_IVF = "video_176x144_vp8_basic.ivf";
    private static final long DEFAULT_TIMEOUT_US = 5000;
    private ByteBuffer[] mInputBuffers,mOutputBuffers;
    int mFrameRate,mFrameWidth,mFrameHeight,mFrameIndex;
    MediaFormat mFormat;
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    MediaCodec mEncoder;

    VP8Encoder2(){
        mFrameRate = 30;
        mFrameWidth = 0;
        mFrameWidth = 0;
        mFrameIndex = 0;
    }
    private static void listCodecs() {
        int numCodecs = MediaCodecList.getCodecCount();

        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder()) {
                continue;
            }
            Log.d("codec",codecInfo.getName());

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
    public void initEncoder(){
        listCodecs();
//        mFrameWidth = 1024;
//        mFrameHeight = 1024;
        MediaFormat format = MediaFormat.createVideoFormat(VP8_MIME, mFrameWidth, mFrameHeight);
//        format.setInteger(MediaFormat.KEY_BIT_RATE, 100000);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,     MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
//        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10);
//        format.setInteger(MediaFormat.KEY_BIT_RATE, 100000);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2);
        format.setInteger(MediaFormat.KEY_BIT_RATE, 3500);

        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0); // bug: http://stackoverflow.com/questions/21284874/illegal-state-exception-when-calling-mediacodec-configure
//        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
//                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
//        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,21);

//        mMediaCodec.configure(mMediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
//        mMediaCodec.start();
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        Log.d(TAG, "Creating encoder");
        try {
            mEncoder = MediaCodec.createByCodecName(VPX_ENCODER_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mEncoder.configure(format,
                null,  // surface
                null,  // crypto
                MediaCodec.CONFIGURE_FLAG_ENCODE);

        mEncoder.start();
        mInputBuffers = mEncoder.getInputBuffers();
        mOutputBuffers = mEncoder.getOutputBuffers();
    }

    public void pushFrameToEncoder(byte[] frame) {

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

    public byte[] popFrameFromEncoder() {
        int result = mEncoder.dequeueOutputBuffer(mBufferInfo, DEFAULT_TIMEOUT_US);
        byte[] buffer = new byte[mBufferInfo.size];
        if (result >= 0) {
            int outputBufIndex = result;
            mOutputBuffers[outputBufIndex].rewind();
            mOutputBuffers[outputBufIndex].get(buffer, 0, mBufferInfo.size);
//
//            if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
//                sawOutputEOS = true;
//            } else {
//                ivf.writeFrame(buffer, mBufferInfo.presentationTimeUs);
//            }
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
            initEncoder();
        }
    }
}
*/