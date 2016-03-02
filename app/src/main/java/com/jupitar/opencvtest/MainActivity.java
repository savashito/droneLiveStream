package com.jupitar.opencvtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.jupitar.internet.ConfigUDPConection;
import com.jupitar.vp8.VP8WebEncoder;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

//import com.jupitar.internet.SocketUDP;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private byte [] mbImage;
//    SocketStreamClient mClientStream;
    VP8WebEncoder mVP8WebEncoder;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.SetCaptureFormat(21);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
        mbImage = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
        initListeners();
        Log.d(TAG, "onCreate");
    }
    protected void initListeners() {
        Button b = (Button) findViewById(R.id.button);

        ConfigUDPConection config = new ConfigUDPConection();
//        mClientStream = new SocketStreamClient(config);
        mVP8WebEncoder = new VP8WebEncoder(config);
        mVP8WebEncoder.start();
//        mClientStream.start();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("fd", "Miau envio");

            }
        });
    }
    static{ System.loadLibrary("opencv_java3"); }


    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OnDestroy");
//        mClientStream.close();
        mVP8WebEncoder.close();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
      
        Mat in = inputFrame.rgba();
        Mat out = new Mat();
        Imgproc.cvtColor(in,out, Imgproc.COLOR_BGRA2YUV_I420);
        Log.d(TAG, String.format("New size: %d * %d", out.channels(), out.total()));

//        cvtColor(original_image, converted_image, CV_BGR2YCrCb);
//        Log.d(TAG,String.format("New size: %d * %d",in.channels(),in.total()));
        byte[] imageInBytes = getByteImageArray(out);
        in.get(0, 0, imageInBytes);
        mVP8WebEncoder.setFrameSize((int) in.size().width, (int) in.size().height);
        mVP8WebEncoder.send(imageInBytes);
        byte [] decodedFrame = mVP8WebEncoder.popDecodedFrame();
        if(decodedFrame!=null){
            Log.d(TAG,String.format("Decoded FRame lenght %d",decodedFrame.length));
            //in.se = decodedFrame;
        }


//        mClientStream.send(imageInBytes);
       // Log.e("fd",imageInBytes.length+"");
        /*
        Size sOut = new Size();
        sOut.height = in.size().height/4.0f;
        sOut.width = in.size().width/4.0f;
        Log.e(TAG, "onCameraFrame: Wid %f"+sOut.width+ " " +sOut.height  );
        Mat out = new Mat(sOut,in.type());*/
       // Imgproc.resize(in, out, out.size());//,0,0,Imgproc.INTER_LINEAR);
//        Mat out;
      //  Imgproc.medianBlur(in,out,51);
        return in;
    }

    private byte[] getByteImageArray(Mat in) {
        if(mbImage==null)
            mbImage = new byte[((int)(in.total())) * in.channels()];
        return mbImage;
    }
}
