package com.allere.handwriterecognize;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

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
    public final String LABEL_CHARACTER_ASSET_LOCATION = "svm-model/label_character_map.txt";

    static {
        System.loadLibrary("handwrite-recognize-lib");
    }


    public int recognizeBase64FormatImg(String base64FormatImg, Context activityContext) {
        Bitmap bitmapImg = this.getBitMap(base64FormatImg);
        Mat mat = new Mat(bitmapImg.getHeight(), bitmapImg.getWidth(), CvType.CV_8UC4);
        bitmapToMat(bitmapImg, mat);
//        int[][] res=this.recognizeMulti(mat.getNativeObjAddr(),HandWriteRecognizer.getSvmModelFilePath(activityContext));
//        Log.d(TAG,res.toString());
//        Log.d(TAG,"AAAAAAAAAAAAAAAAAAAAAA");
//        return 1;
        return this.recognize(mat.getNativeObjAddr(), activityContext.getExternalFilesDir("") + "/" + HandWriteRecognizer.SVM_MODEL_FILE);
//      return this.recognize(mat.getNativeObjAddr(),HandWriteRecognizer.getSvmModelFilePath(activityContext));
    }

    /**
     * 将一张图片按图片上的文字间隔进行分隔，并将分割后的图片进行识别，并将
     * 识别结果以二维数组的形式返回，如：
     * 图片上的文字位置如下：
     * 1  2  3  4
     * 5    6
     * 7 8   9
     * 则识别数组的结果为 :
     * [["1","2","3","4"],["5","6"],["7","8","9"]]
     *
     * @param base64FormatImg
     * @param activityContext
     * @return
     */
    public int[][] recognizeMultipleBase64FormatImg(String base64FormatImg, Context activityContext) {
        Bitmap bitmapImg = this.getBitMap(base64FormatImg);
        Mat mat = new Mat(bitmapImg.getHeight(), bitmapImg.getWidth(), CvType.CV_8UC4);
        bitmapToMat(bitmapImg, mat);

        String testFileDir = activityContext.getExternalFilesDir("") + "/" + FileOperator.TEST_IMAGES_DIR;
        String lastRecognizeImg = activityContext.getExternalFilesDir("") + "/" + FileOperator.TEST_IMAGES_DIR + "/last.bmp";
        try {
            File testFile = new File(testFileDir);
            if (!testFile.exists()) {
                testFile.mkdir();
            } else {
                File[] fileList = testFile.listFiles();
                for (int i = 0; i < fileList.length; i++) {
                    fileList[i].delete();
                }
            }
            File last = new File(lastRecognizeImg);
            FileOutputStream out = new FileOutputStream(last);
            bitmapImg.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return this.recognizeMulti(mat.getNativeObjAddr()
//                , activityContext.getExternalFilesDir("") + "/" + HandWriteRecognizer.SVM_MODEL_FILE
                , activityContext.getFilesDir().getAbsolutePath() + "/" + HandWriteRecognizer.SVM_MODEL_FILE
                , testFileDir);
//        return this.recognizeMulti(mat.getNativeObjAddr(), HandWriteRecognizer.getSvmModelFilePath(activityContext));
    }

    /**
     * 保存训练样本图片
     *
     * @param trainVal
     * @param img
     * @param savePath
     */
    public void setTrainImg(final String trainVal, String img, String savePath) {
        File dir = new File(savePath);
        if (!dir.exists()) dir.mkdir();

        int existLen = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.startsWith(trainVal);
            }
        }).length;
        String fileName = trainVal + "_" + (existLen + 1) + ".bmp";

        if (img.contains(this.BASE64_STR_CHARACTER)) {
            Bitmap bitmapImg = this.getBitMap(img);
            Mat mat = new Mat(bitmapImg.getHeight(), bitmapImg.getWidth(), CvType.CV_8UC4);
            bitmapToMat(bitmapImg, mat);
            this.saveTrainImage(mat.getNativeObjAddr(), savePath + "/" + fileName);
        } else {
            File imgFile = new File(img);
            imgFile.renameTo(new File(savePath + "/" + fileName));
        }
    }

    public static String getSvmModelFilePath(Context activityContext) {
        String filesDir = activityContext.getFilesDir().getAbsolutePath();
        return filesDir + "/" + SVM_MODEL_FILE;
    }

    public Bitmap getBitMap(String base64Str) {
        base64Str = base64Str.substring(base64Str.indexOf(this.BASE64_STR_CHARACTER) + this.BASE64_STR_CHARACTER.length(), base64Str.length());
        byte bytes[] = Base64.decode(base64Str, Base64.DEFAULT);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
    }


    public native int recognize(long imgAddress, String svm_model_path);

    public native int[][] recognizeMulti(long imgAddress, String svm_model_path, String cut_image_save_dir);

    public native String train(String[] images, String dir, String svm_model_path);

    public native String trainFromMat(Mat[] matList, int[] labels, String svm_model_path);

    public native void testImageOperate(long matAddress, String imageSaveAddress);

    public native void saveTrainImage(long matAddress, String imageSaveAddress);
}
