package com.allere.handwriterecognize;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.List;

import static org.opencv.android.Utils.bitmapToMat;

/**
 * Created by allere on 16/12/21.
 */

public class HandWriteRecognizer {
    private static final String TAG = "HandWriteRecognizer";
    private static final String RETURN_MESSAGE_KEY = "msg";
    private static final String OPENCV_NOT_LOADED = "opencv is not loaded!";
    private static final String OPENCV_AVALIABLE = "opencv is successfully loaded!";
    private static final String BASE64_STR_CHARACTER = "base64,";

    private Bitmap recognizeImg;


    static {
        System.loadLibrary("handwrite-recognize-lib");
    }

    public JSONObject recognizeImg(String base64FormatImg, Context activityContext) throws JSONException {
        JSONObject retObj = new JSONObject();
        this.setRecognizeImg(this.getBitMapFromBase64Str(base64FormatImg));
        BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(activityContext) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                        Log.i(TAG, OPENCV_AVALIABLE);
                        Mat mat = new Mat(getRecognizeImg().getHeight(), getRecognizeImg().getWidth(), CvType.CV_8UC3);
                        bitmapToMat(recognizeImg, mat);
                        recognize(mat.getNativeObjAddr(),getRecognizeImg().getHeight(),getRecognizeImg().getWidth());
                    }
                    break;
                    default: {
                        super.onManagerConnected(status);
                    }
                    break;
                }
            }
        };
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, OPENCV_NOT_LOADED);
            retObj.put(RETURN_MESSAGE_KEY, OPENCV_NOT_LOADED);
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, activityContext, mLoaderCallback);
        } else {
            Log.d(TAG, OPENCV_AVALIABLE);
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        return retObj;
    }

    private Bitmap getBitMapFromBase64Str(String base64Str) {
        base64Str = base64Str.substring(base64Str.indexOf(this.BASE64_STR_CHARACTER) + this.BASE64_STR_CHARACTER.length(), base64Str.length());
        byte bytes[] = Base64.decode(base64Str, Base64.DEFAULT);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
    }


    public Bitmap getRecognizeImg() {
        return recognizeImg;
    }

    public void setRecognizeImg(Bitmap recognizeImg) {
        this.recognizeImg = recognizeImg;
    }

    public native String recognize(long mat,int height,int width);
    public native String train(String[] images,String dir);
}
