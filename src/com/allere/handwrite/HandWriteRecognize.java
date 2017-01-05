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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import io.cordova.hellocordova.Main2Activity;

import static org.opencv.android.Utils.bitmapToMat;

/**
 * Created by pjl on 2016/12/17.
 */

public class HandWriteRecognize extends CordovaPlugin {

    private static final String TAG = "HWRPlugin";
    private final String TRAIN_IMAGE_DIR_NAME = "train-images";
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
        final Context ctx = this.cordova.getActivity();
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
                                    int result = handWriteRecognizer.recognizeBase64FormatImg(base64Str, ctx);
                                    r.put("recognizeResult", result);
                                    Log.d("InstrumentTest", "predict result is --->" + result);
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
            FileOperator optr = new FileOperator(this.cordova.getActivity());
            Context activity = this.cordova.getActivity();
            JSONObject result = new JSONObject();
            try {
                Mat imgMat = optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR + "/0_17.bmp"));
                Mat imgMat1 = optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR + "/1_17.bmp"));
                Mat imgMat2 = optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR + "/2_17.bmp"));
                Mat imgMat3 = optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR + "/3_17.bmp"));
                Mat imgMat4 = optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR + "/4_17.bmp"));
                Mat imgMat5 = optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR + "/5_17.bmp"));
                Mat imgMat6 = optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR + "/6_17.bmp"));
                Mat imgMat7 = optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR + "/7_17.bmp"));
                Mat imgMat8 = optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR + "/8_17.bmp"));
                Mat imgMat9 = optr.convertImg2Mat(activity.getAssets().open(FileOperator.TRAIN_IMAGES_DIR + "/9_17.bmp"));
                optr.createFilesDir(FileOperator.DEBUG_IMAGES);
                handWriteRecognizer.testImageOperate(imgMat.getNativeObjAddr(), activity.getFilesDir().getAbsolutePath() + "/" + FileOperator.DEBUG_IMAGES + "/0_1.bmp");
                handWriteRecognizer.testImageOperate(imgMat1.getNativeObjAddr(), activity.getFilesDir().getAbsolutePath() + "/" + FileOperator.DEBUG_IMAGES + "/1_1.bmp");
                handWriteRecognizer.testImageOperate(imgMat2.getNativeObjAddr(), activity.getFilesDir().getAbsolutePath() + "/" + FileOperator.DEBUG_IMAGES + "/2_1.bmp");
                handWriteRecognizer.testImageOperate(imgMat3.getNativeObjAddr(), activity.getFilesDir().getAbsolutePath() + "/" + FileOperator.DEBUG_IMAGES + "/3_1.bmp");
                handWriteRecognizer.testImageOperate(imgMat4.getNativeObjAddr(), activity.getFilesDir().getAbsolutePath() + "/" + FileOperator.DEBUG_IMAGES + "/4_1.bmp");
                handWriteRecognizer.testImageOperate(imgMat5.getNativeObjAddr(), activity.getFilesDir().getAbsolutePath() + "/" + FileOperator.DEBUG_IMAGES + "/5_1.bmp");
                handWriteRecognizer.testImageOperate(imgMat6.getNativeObjAddr(), activity.getFilesDir().getAbsolutePath() + "/" + FileOperator.DEBUG_IMAGES + "/6_1.bmp");
                handWriteRecognizer.testImageOperate(imgMat7.getNativeObjAddr(), activity.getFilesDir().getAbsolutePath() + "/" + FileOperator.DEBUG_IMAGES + "/7_1.bmp");
                handWriteRecognizer.testImageOperate(imgMat8.getNativeObjAddr(), activity.getFilesDir().getAbsolutePath() + "/" + FileOperator.DEBUG_IMAGES + "/8_1.bmp");
                handWriteRecognizer.testImageOperate(imgMat9.getNativeObjAddr(), activity.getFilesDir().getAbsolutePath() + "/" + FileOperator.DEBUG_IMAGES + "/9_1.bmp");
                String[] files = optr.getFilesDirFileNames(FileOperator.DEBUG_IMAGES, true);
                JSONArray ary = new JSONArray();
                for (int i = 0; i < files.length; i++) {
                    ary.put(files[i]);
                }
                result.put("list", ary);
                callbackContext.success(result);
            } catch (Exception e) {
                result.put("message", e.getMessage());
                callbackContext.error(result);
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        } else if ("setTrainImage".equals(action)) {
            JSONArray ary = this.setTrainImage(args);
            callbackContext.success(ary);
        } else if ("deleteTrainImage".equals(action)) {
            JSONArray ary = this.deleteTrainImage(args);
            callbackContext.success(ary);
        } else if("getTrainImageList".equals(action)){
            JSONArray ary = this.getTrainImageJSONArray();
            callbackContext.success(ary);
        }else if("trainFromTrainImages".equals(action)){
            JSONObject obj = this.trainFromTrainImages();
            callbackContext.success(obj);
        }else if("getLabelCharacterMap".equals(action)){
            JSONObject obj = this.getLabelCharacterMap();
            callbackContext.success(obj);
        }else {
            return false;
        }


        return true;
    }

    /**
     * 获取训练样本图片地址
     * @return
     */
    private JSONArray getTrainImageJSONArray(){
        JSONArray ary = new JSONArray();
        Context ctx=this.cordova.getActivity();
        //读取重命名后的文件列表并返回
        String[] fileNames = ctx.getExternalFilesDir(TRAIN_IMAGE_DIR_NAME).list();
        for (int i = 0; i < fileNames.length; i++) {
            ary.put(ctx.getExternalFilesDir(TRAIN_IMAGE_DIR_NAME).getAbsolutePath() + "/" + fileNames[i]);
        }
        return ary;
    }

    /**
     * 添加训练样本图片
     * @param args
     * @return
     * @throws JSONException
     */
    public JSONArray setTrainImage(JSONArray args) throws JSONException {
        //cast trainVal
        String trainVal;
        if (args.get(0) instanceof Integer) {
            trainVal = Integer.toString((Integer) args.get(0));
        } else {
            trainVal = (String) args.get(0);
        }
        //get base64 image
        String base64Img = (String) args.get(1);

        HandWriteRecognizer handWriteRecognizer = new HandWriteRecognizer();
        Context ctx = this.cordova.getActivity();
        File dir = new File(ctx.getExternalFilesDir(TRAIN_IMAGE_DIR_NAME).getAbsolutePath());
        if (!dir.exists()) dir.mkdir();
        handWriteRecognizer.setBase64FormatTrainImg(trainVal, base64Img, ctx, dir.getAbsolutePath());
        return this.getTrainImageJSONArray();
    }

    /**
     * 删除训练样本图片
     * @param args
     * @return
     * @throws JSONException
     */
    public JSONArray deleteTrainImage(JSONArray args) throws JSONException {
        Context ctx = this.cordova.getActivity();
        String imgPath = (String) args.get(0);
        //获取trainVal
        String[] temp=imgPath.split("/");
        String imageName=temp[temp.length-1];
        final String trainVal=imageName.split("_")[0];
        //删除图片
        File imgFile = new File(imgPath);
        imgFile.delete();
        //为trainVal相关图片重命名
        String[] relatedImgs=ctx.getExternalFilesDir(TRAIN_IMAGE_DIR_NAME).list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.startsWith(trainVal);
            }
        });
        for(int i=0;i<relatedImgs.length;i++){
            File file=new File(ctx.getExternalFilesDir(TRAIN_IMAGE_DIR_NAME).getAbsolutePath()+"/"+relatedImgs[i]);
            file.renameTo(new File(ctx.getExternalFilesDir(TRAIN_IMAGE_DIR_NAME).getAbsolutePath()+"/"+trainVal+"_"+i+".bmp"));
        }
        return this.getTrainImageJSONArray();
    }

    /**
     * 根据训练图片进行svm训练，并生成训练模型文件
     * @return
     * @throws JSONException
     */
    public JSONObject trainFromTrainImages() throws JSONException {
        JSONObject rt=new JSONObject();
        Context ctx=this.cordova.getActivity();
        File trainImageFolder=ctx.getExternalFilesDir(TRAIN_IMAGE_DIR_NAME);
        String svmModelPath=ctx.getExternalFilesDir("").getAbsolutePath()+"/"+HandWriteRecognizer.SVM_MODEL_FILE;
        FileOperator optr=new FileOperator(ctx);
        String[] trainImageNames=trainImageFolder.list();
        Mat[] images=new Mat[trainImageNames.length];
        int[] labels=new int[trainImageNames.length];
        for(int i=0;i<trainImageNames.length;i++){
            String trainImageName=trainImageNames[i];
            images[i]=optr.convertImg2Mat(ctx.getExternalFilesDir(TRAIN_IMAGE_DIR_NAME)+"/"+trainImageNames[i]);
            labels[i]=Integer.parseInt(trainImageName.split("_")[0]);
        }
        handWriteRecognizer.trainFromMat(images, labels, svmModelPath);
        rt.put("svmPath",svmModelPath);
        return rt;
    }

    public JSONObject getLabelCharacterMap() throws JSONException{
        JSONObject obj=new JSONObject();
        JSONArray mapList =new JSONArray();
        String errorMsg;

        try {
            InputStream fileLabelCharacterMap =this.getActivity().getAssets().open(handWriteRecognizer.LABEL_CHARACTER_ASSET_LOCATION);
            byte[] buffer=new byte[fileLabelCharacterMap.available()];
            fileLabelCharacterMap.read(buffer);
            String mapStr=new String(buffer);
            mapStr=mapStr.replace("\r","");
            String[] mapItemList=mapStr.split("\n");
            //数字与文字的map长度应该大于2
            if(mapItemList.length<2){
                errorMsg="parseError:label character map length should > 2";
                obj.put("errorMsg",errorMsg);
                Log.e(TAG,errorMsg);
                return obj;
            }
            for(int i=0;i<mapItemList.length;i++){
                String mapItem=mapItemList[i];
                String[] map=mapItem.split(":");
                if(map.length!=2){
                    errorMsg="parseError:label character map ";
                    obj.put("errorMsg",errorMsg);
                    Log.e(TAG,errorMsg);
                    return  obj;
                }
                try{
                   Integer.parseInt(map[0]);
                }catch (Exception e){
                    errorMsg="parserError:label should be Integer,got "+map[0];
                    obj.put("errorMsg",errorMsg);
                    Log.e(TAG,errorMsg);
                    return obj;
                }
                JSONObject mapJSONItem=new JSONObject();
                mapJSONItem.put("label",map[0]);
                mapJSONItem.put("character",map[1]);
                mapList.put(mapJSONItem);
            }
            obj.put("labelCharacterList",mapList);
        } catch (IOException e) {
            Log.d(TAG,e.getMessage());
            e.printStackTrace();
            obj.put("errorMsg",e.getMessage());
        }
        return obj;
    }

    public void toDebugActivity(String base64Str) {
        Intent intent = new Intent();
        intent.setClass(this.cordova.getActivity(), Main2Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("imgData", base64Str);
        intent.putExtras(bundle);
        this.cordova.getActivity().startActivity(intent);
    }

    public Context getActivity(){
        return this.cordova.getActivity();
    }


}
