package com.jupitar.vp8;

/**
 * Created by rodrigosavage on 2/15/16.
 */

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;



/**
 * Created by rodrigosavage on 2/24/16.
 */
public class VP8Encoder {



    private static final String TAG = "VP8EncoderTest";
    private static final String VP8_MIME = "video/x-vnd.on2.vp8";
    //    private static final String VPX_DECODER_NAME = "OMX.google.vpx.decoder";
    private static final String VPX_ENCODER_NAME = "OMX.google.vpx.encoder";
    //    private static final String BASIC_IVF = "video_176x144_vp8_basic.ivf";
    private static final long DEFAULT_TIMEOUT_US = 5000;
    private ByteBuffer[] mInputBuffers,mOutputBuffers;
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    private void encode( InputStream rawStream,// int rawInputFd,
                         int frameWidth, int frameHeight, int frameRate)throws IOException {
        // why divided by two?
        int frameSize = frameWidth * frameHeight * 3 / 2;
        // Create a media format signifying desired output
        MediaFormat format = MediaFormat.createVideoFormat(VP8_MIME, frameWidth, frameHeight);
        format.setInteger(MediaFormat.KEY_BIT_RATE, 100000);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        Log.d(TAG, "Creating encoder");
        MediaCodec encoder;
        encoder = MediaCodec.createByCodecName(VPX_ENCODER_NAME);
        encoder.configure(format,
                null,  // surface
                null,  // crypto
                MediaCodec.CONFIGURE_FLAG_ENCODE);
        encoder.start();
        mInputBuffers = encoder.getInputBuffers();
        mOutputBuffers = encoder.getOutputBuffers();
        IvfWriter ivf = null;
        try {
//            rawStream = mResources.openRawResource(rawInputFd);
            ivf = new IvfWriter(frameWidth, frameHeight);
            // encode loop
            long presentationTimeUs = 0;
            int frameIndex = 0;
            boolean sawInputEOS = false;
            boolean sawOutputEOS = false;
            while (!sawOutputEOS) {
                if (!sawInputEOS) {
                    // here we insert a frame to be encoded
                    int inputBufIndex = encoder.dequeueInputBuffer(DEFAULT_TIMEOUT_US);
                    if (inputBufIndex >= 0) {
                        byte[] frame = new byte[frameSize];
                        int bytesRead = rawStream.read(frame);
                        if (bytesRead == -1) {
                            sawInputEOS = true;
                            bytesRead = 0;
                        }
                        mInputBuffers[inputBufIndex].clear();
                        mInputBuffers[inputBufIndex].put(frame);
                        mInputBuffers[inputBufIndex].rewind();
                        presentationTimeUs = (frameIndex * 1000000) / frameRate;
                        Log.d(TAG, "Encoding frame at index " + frameIndex);
                        encoder.queueInputBuffer(
                                inputBufIndex,
                                0,  // offset
                                bytesRead,  // size
                                presentationTimeUs,
                                sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);
                        frameIndex++;
                    }
                }
                // here we extract the encoded frame and save it in the IvfWriter
                int result = encoder.dequeueOutputBuffer(mBufferInfo, DEFAULT_TIMEOUT_US);
                if (result >= 0) {
                    int outputBufIndex = result;
                    byte[] buffer = new byte[mBufferInfo.size];
                    mOutputBuffers[outputBufIndex].rewind();
                    mOutputBuffers[outputBufIndex].get(buffer, 0, mBufferInfo.size);
                    if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        sawOutputEOS = true;
                    } else {
                        ivf.writeFrame(buffer, mBufferInfo.presentationTimeUs);
                    }
                    encoder.releaseOutputBuffer(outputBufIndex,
                            false);  // render
                } else if (result == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    mOutputBuffers = encoder.getOutputBuffers();
                }
            }
            encoder.stop();
            encoder.release();
        } finally {
            if (ivf != null) {
                ivf.close();
            }
            if (rawStream != null) {
                rawStream.close();
            }
        }
    }

}
