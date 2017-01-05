package com.allere.handwriterecognize;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
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

import java.io.File;
import java.io.FilenameFilter;
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
    public static final String SVM_MODEL_FILE = "handwrite_trained_result.yml";

    static {
        System.loadLibrary("handwrite-recognize-lib");
    }



    public int recognizeBase64FormatImg(String base64FormatImg,Context activityContext)  {
        Bitmap bitmapImg = this.getBitMapFromBase64Str(base64FormatImg);
        Mat mat = new Mat(bitmapImg.getHeight(), bitmapImg.getWidth(), CvType.CV_8UC4);
        bitmapToMat(bitmapImg, mat);
//        return this.recognize(mat.getNativeObjAddr(),activityContext.getExternalFilesDir("")+"/"+HandWriteRecognizer.SVM_MODEL_FILE);
        return this.recognize(mat.getNativeObjAddr(),HandWriteRecognizer.getSvmModelFilePath(activityContext));
    }

    public void setBase64FormatTrainImg(final String trainVal, String base64FormatTrainImg, Context activityContext, String savePath){
        Bitmap bitmapImg = this.getBitMapFromBase64Str(base64FormatTrainImg);
        Mat mat = new Mat(bitmapImg.getHeight(), bitmapImg.getWidth(), CvType.CV_8UC4);
        bitmapToMat(bitmapImg, mat);
        File dir=new File(savePath);
        if(!dir.exists())dir.mkdir();
        int existLen=dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.startsWith(trainVal);
            }
        }).length;
        String fileName=trainVal+"_"+(existLen+1)+".bmp";
        this.saveTrainImage(mat.getNativeObjAddr(),savePath+"/"+fileName);
    }

    public static String getSvmModelFilePath(Context activityContext) {
        String filesDir = activityContext.getFilesDir().getAbsolutePath();
        return filesDir + "/" + SVM_MODEL_FILE;
    }

    private Bitmap getBitMapFromBase64Str(String base64Str) {
        base64Str = base64Str.substring(base64Str.indexOf(this.BASE64_STR_CHARACTER) + this.BASE64_STR_CHARACTER.length(), base64Str.length());
        byte bytes[] = Base64.decode(base64Str, Base64.DEFAULT);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
    }



    public native int recognize(long imgAddress, String svm_model_path);

    public native String train(String[] images, String dir, String svm_model_path);

    public native String trainFromMat(Mat[] matList, int[] labels, String svm_model_path);

    public native void testImageOperate(long matAddress,String imageSaveAddress);

    public native void saveTrainImage(long matAddress,String imageSaveAddress);
}
