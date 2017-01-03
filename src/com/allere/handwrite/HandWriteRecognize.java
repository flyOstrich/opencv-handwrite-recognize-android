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
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
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

    private static  final  String TAG="HWRPlugin";
    private HandWriteRecognizer handWriteRecognizer = new HandWriteRecognizer();

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this.cordova.getActivity(), new BaseLoaderCallback(this.cordova.getActivity()) {
                @Override
                public void onManagerConnected(int status) {
                    super.onManagerConnected(status);
                }
            });
        }
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        final Context ctx=this.cordova.getActivity();
        if ("getHandWriteInfo".equals(action)) {
            if (args.length() != 0) {
                final JSONObject r = new JSONObject();
                final String base64Str = (String) args.get(0);
//                this.handWriteRecognizer.recognizeImg(base64Str,this.cordova.getActivity());
                BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(ctx) {
                    @Override
                    public void onManagerConnected(int status) {
                        switch (status) {
                            case LoaderCallbackInterface.SUCCESS: {
                                try {
                                    FileOperator optr = new FileOperator(ctx);
                                    optr.moveSvmModelFromAssetsDirToFilesDir(ctx);
                                    HandWriteRecognizer handWriteRecognizer = new HandWriteRecognizer();
                                    int result=handWriteRecognizer.recognizeBase64FormatImg(base64Str,ctx);
                                    r.put("recognizeResult",result);
                                    Log.d("InstrumentTest","predict result is --->"+result );
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

//                toDebugActivity(base64Str);
                r.put("status", "true");
                callbackContext.success(r);
            }
        } else if ("showImageList".equals(action)) {
            FileOperator optr=new FileOperator(this.cordova.getActivity());
            Context activity=this.cordova.getActivity();
            JSONObject result = new JSONObject();
            try {
                Mat imgMat=optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR+ "/0_10.bmp"));
                Mat imgMat1=optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR+"/1_10.bmp"));
                Mat imgMat2=optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR+"/2_10.bmp"));
                Mat imgMat3=optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR+"/3_10.bmp"));
                Mat imgMat4=optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR+"/4_10.bmp"));
                Mat imgMat5=optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR+"/5_10.bmp"));
                Mat imgMat6=optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR+"/6_10.bmp"));
                Mat imgMat7=optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR+"/7_10.bmp"));
                Mat imgMat8=optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR+"/8_10.bmp"));
                Mat imgMat9=optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR+"/9_10.bmp"));
                optr.createFilesDir(FileOperator.DEBUG_IMAGES);
                handWriteRecognizer.testImageOperate(imgMat.getNativeObjAddr(),activity.getFilesDir().getAbsolutePath()+"/"+FileOperator.DEBUG_IMAGES+"/0_1.bmp");
                handWriteRecognizer.testImageOperate(imgMat1.getNativeObjAddr(),activity.getFilesDir().getAbsolutePath()+"/"+FileOperator.DEBUG_IMAGES+"/1_1.bmp");
                handWriteRecognizer.testImageOperate(imgMat2.getNativeObjAddr(),activity.getFilesDir().getAbsolutePath()+"/"+FileOperator.DEBUG_IMAGES+"/2_1.bmp");
                handWriteRecognizer.testImageOperate(imgMat3.getNativeObjAddr(),activity.getFilesDir().getAbsolutePath()+"/"+FileOperator.DEBUG_IMAGES+"/3_1.bmp");
                handWriteRecognizer.testImageOperate(imgMat4.getNativeObjAddr(),activity.getFilesDir().getAbsolutePath()+"/"+FileOperator.DEBUG_IMAGES+"/4_1.bmp");
                handWriteRecognizer.testImageOperate(imgMat5.getNativeObjAddr(),activity.getFilesDir().getAbsolutePath()+"/"+FileOperator.DEBUG_IMAGES+"/5_1.bmp");
                handWriteRecognizer.testImageOperate(imgMat6.getNativeObjAddr(),activity.getFilesDir().getAbsolutePath()+"/"+FileOperator.DEBUG_IMAGES+"/6_1.bmp");
                handWriteRecognizer.testImageOperate(imgMat7.getNativeObjAddr(),activity.getFilesDir().getAbsolutePath()+"/"+FileOperator.DEBUG_IMAGES+"/7_1.bmp");
                handWriteRecognizer.testImageOperate(imgMat8.getNativeObjAddr(),activity.getFilesDir().getAbsolutePath()+"/"+FileOperator.DEBUG_IMAGES+"/8_1.bmp");
                handWriteRecognizer.testImageOperate(imgMat9.getNativeObjAddr(),activity.getFilesDir().getAbsolutePath()+"/"+FileOperator.DEBUG_IMAGES+"/9_1.bmp");
                String[] files= optr.getFilesDirFileNames(FileOperator.DEBUG_IMAGES,true);
                JSONArray ary=new JSONArray();
                for(int i=0;i<files.length;i++){
                    ary.put(files[i]);
                }
                result.put("list",ary);
                callbackContext.success(result);
            } catch (Exception e) {
                result.put("message",e.getMessage());
                callbackContext.error(result);
                Log.e(TAG,e.getMessage());
                e.printStackTrace();
            }
        }else{
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
