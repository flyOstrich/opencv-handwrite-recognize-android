package com.allere.handwrite;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.allere.handwriterecognize.FileOperator;
import com.allere.handwriterecognize.HandWriteRecognizer;

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
import java.util.Map;

import io.cordova.hellocordova.Main2Activity;

import static org.opencv.android.Utils.bitmapToMat;

/**
 * Created by pjl on 2016/12/17.
 */

public class HandWriteRecognize extends CordovaPlugin {


    private HandWriteRecognizer handWriteRecognizer = new HandWriteRecognizer();

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        final Context ctx=this.cordova.getActivity();
        if ("getHandWriteInfo".equals(action)) {
            if (args.length() != 0) {
                String base64Str = (String) args.get(0);
//                this.handWriteRecognizer.recognizeImg(base64Str,this.cordova.getActivity());
                BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(ctx) {
                    @Override
                    public void onManagerConnected(int status) {
                        switch (status) {
                            case LoaderCallbackInterface.SUCCESS: {
                                try {
                                    FileOperator optr = new FileOperator(ctx);
                                    HandWriteRecognizer handWriteRecognizer = new HandWriteRecognizer();
                                    //train
                                    String[] files = optr.getAssetFileNames(FileOperator.TRAIN_IMAGES_DIR);
                                    Map res=optr.ReadFilesMat(files, FileOperator.TRAIN_IMAGES_DIR);
                                    handWriteRecognizer.trainFromMat((Mat[])res.get("images"),(int[]) res.get("labels"),handWriteRecognizer.getSvmModelFilePath(ctx));

                                } catch (Exception e) {
                                    Log.e("InstrumentTest", e.getMessage());
                                }
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
                    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, ctx, mLoaderCallback);
                } else {
                    mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
                }

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


}
