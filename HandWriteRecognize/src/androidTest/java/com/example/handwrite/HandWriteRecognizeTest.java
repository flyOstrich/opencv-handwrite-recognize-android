package com.example.handwrite;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.allere.handwriterecognize.FileOperator;
import com.allere.handwriterecognize.HandWriteRecognizer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.InputStream;

import static org.opencv.android.Utils.bitmapToMat;


/**
 * Created by allere on 16/12/21.
 */
@RunWith(AndroidJUnit4.class)
public class HandWriteRecognizeTest {
    private HandWriteRecognizer handWriteRecognizer = new HandWriteRecognizer();
    private FileOperator optr = null;
    private Context ctx = null;

    @Before
    public void initOpenCv() {
        this.ctx = InstrumentationRegistry.getInstrumentation().getContext();
        this.optr = new FileOperator(ctx);
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this.ctx, null);
        }
    }

    @Test
    public void testRecognizeBase64Img() throws Exception {
        InputStream in = this.ctx.getAssets().open("testBase64/testRecognizeBase64.txt");
        byte[] buffer =new byte[in.available()];
        in.read(buffer);
        String base64Img=new String(buffer);
        optr.moveSvmModelFromAssetsDirToFilesDir(ctx);
        handWriteRecognizer.recognizeMultipleBase64FormatImg(base64Img,ctx);
    }
    @Test
    public void testImgOperate() throws Exception {
        InputStream in = this.ctx.getAssets().open("testBase64/testRecognizeBase64.txt");
        byte[] buffer =new byte[in.available()];
        in.read(buffer);
        String base64Img=new String(buffer);
        Bitmap imgBitMap=handWriteRecognizer.getBitMap(base64Img);
        Mat mat = new Mat(imgBitMap.getHeight(), imgBitMap.getWidth(), CvType.CV_8UC4);
        bitmapToMat(imgBitMap,mat);
        handWriteRecognizer.testImageOperate(mat.getNativeObjAddr(),"");
    }


}
