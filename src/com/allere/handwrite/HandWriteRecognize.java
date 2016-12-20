package com.allere.handwrite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.List;

import io.cordova.hellocordova.Main2Activity;

import static org.opencv.android.Utils.bitmapToMat;

/**
 * Created by pjl on 2016/12/17.
 */

public class HandWriteRecognize extends CordovaPlugin {

    private static final String TAG = "HandWriteRecognize";
    private static final String RETURN_MESSAGE_KEY = "msg";
    private static final String OPENCV_NOT_LOADED = "opencv is not loaded!";
    private static final String OPENCV_AVALIABLE = "opencv is successfully loaded!";

    private String BASE64_STR_CHARACTER = "base64,";
    private Bitmap recognizeImg = null;
    private List<String> recognizeResult = null;

    static {
        System.loadLibrary("native-lib");
    }

//    private BaseLoaderCallback mLoaderCallback=

    private Bitmap getBitMapFromBase64Str(String base64Str) {
        base64Str = base64Str.substring(base64Str.indexOf(this.BASE64_STR_CHARACTER) + this.BASE64_STR_CHARACTER.length(), base64Str.length());
        byte bytes[] = Base64.decode(base64Str, Base64.DEFAULT);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("getHandWriteInfo".equals(action)) {
            if (args.length() != 0) {
                String base64Str = (String) args.get(0);
                this.recognizeImg(base64Str);
                toDebugActivity(base64Str);

                JSONObject r = new JSONObject();
                r.put("status", "true");
                callbackContext.success(r);
            }
        } else {
            return false;
        }
        return true;
    }

    public void toDebugActivity(String base64Str) {
        Intent intent = new Intent();
        intent.setClass(this.cordova.getActivity(), Main2Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("imgData", base64Str);
        intent.putExtras(bundle);
        this.cordova.getActivity().startActivity(intent);
    }

    public JSONObject recognizeImg(String base64Str) throws JSONException {
        JSONObject retObj = new JSONObject();
        this.recognizeImg = this.getBitMapFromBase64Str(base64Str);
        BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this.cordova.getActivity()) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                        Log.i(TAG, "OpenCV loaded successfully");
                        Mat mat = new Mat(recognizeImg.getHeight(), recognizeImg.getWidth(), CvType.CV_8UC4);
                        bitmapToMat(recognizeImg, mat);
                        recognize(mat.getNativeObjAddr());
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
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this.cordova.getActivity(), mLoaderCallback);
        } else {
            Log.d(TAG, OPENCV_AVALIABLE);
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        return retObj;
    }

    public native String recognize(long mat);
}
