package com.example.handwrite;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.allere.handwriterecognize.FileOperator;
import com.allere.handwriterecognize.HandWriteRecognizer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.runner.RunWith;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by allere on 16/12/21.
 */
@RunWith(AndroidJUnit4.class)
public class FileOperatorTest {
    @Test
    public void testGetFilesDirFilesNames() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        FileOperator operator = new FileOperator(appContext);
        operator.createFilesDir(FileOperator.TEST_IMAGES_DIR);
        File f = new File(appContext.getFilesDir().getAbsolutePath() + "/" + FileOperator.TEST_IMAGES_DIR + "/" + "testFile.txt");
        FileOutputStream out = new FileOutputStream(f);
        String a = "test file";
        out.write(a.getBytes());
        out.close();
        String[] res = operator.getFilesDirFileNames(FileOperator.TEST_IMAGES_DIR, true);
    }

    @Test
    public void testConvertImg2Mat() throws IOException {
       final Context appContext = InstrumentationRegistry.getTargetContext();

        BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(appContext) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                        InputStream imgFileInputStream = null;
                        try {
                            imgFileInputStream = appContext.getAssets().open(FileOperator.TRAIN_IMAGES_DIR+"/0_1.bmp");
                            FileOperator operator = new FileOperator(appContext);
                            Mat imgMat=operator.convertImg2Mat(imgFileInputStream);
                            HandWriteRecognizer handWriteRecognizer=new HandWriteRecognizer();
                            handWriteRecognizer.testImageOperate(imgMat.getNativeObjAddr(),appContext.getExternalFilesDir("").getAbsolutePath()+"/"+FileOperator.DEBUG_IMAGES+"/0_1.bmp");
                            Assert.assertEquals(imgMat.rows(),28);
                        } catch (IOException e) {
                            e.printStackTrace();
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
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, appContext, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}
