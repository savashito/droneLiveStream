package com.jupitar.opencvtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.jupitar.internet.ConfigUDPConection;
import com.jupitar.internet.SocketStreamClient;
//import com.jupitar.internet.SocketUDP;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private byte [] mbImage;
    SocketStreamClient mClientStream;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
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
        mClientStream = new SocketStreamClient(config);
        mClientStream.start();
//        mServerConection = new SocketUDP();
//        mServerConection.execute(config);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("fd", "Miau envio");
               // mClientStream.send("Miauuu");
//                mServerConection.send("Miauuu");

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
        byte[] imageInBytes = getByteImageArray(in);
        in.get(0, 0, imageInBytes);
      //  mServerConection.send("Bark!! and this is a longer bark!!");
//        mServerConection.send(imageInBytes);
        mClientStream.send(imageInBytes);
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
