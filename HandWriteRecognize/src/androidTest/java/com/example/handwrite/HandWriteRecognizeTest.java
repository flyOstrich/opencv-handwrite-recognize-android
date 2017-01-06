package com.example.handwrite;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.allere.handwriterecognize.FileOperator;
import com.allere.handwriterecognize.HandWriteRecognizer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.opencv.android.Utils.bitmapToMat;

/**
 * Created by allere on 16/12/21.
 */
@RunWith(AndroidJUnit4.class)
public class HandWriteRecognizeTest {
    @Test
    public void TestRecognizeBase64Str() throws Exception {
        //包含1，2，3，4的base64图像字符串
        Context appContext = InstrumentationRegistry.getTargetContext();


//        HandWriteRecognizer handWriteRecognizer = new HandWriteRecognizer();
//        handWriteRecognizer.recognizeImg(base64FormatStr, appContext);

    }

    @Test
    public void TestReadImgMatListAndTrain() {
        final Context appContext = InstrumentationRegistry.getInstrumentation().getContext();


        BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(appContext) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                        try {
                            FileOperator optr = new FileOperator(appContext);
                            HandWriteRecognizer handWriteRecognizer = new HandWriteRecognizer();
                            //train
                            String[] files = optr.getAssetFileNames(FileOperator.TRAIN_IMAGES_DIR);
                            Map res = optr.ReadFilesMat(files, FileOperator.TRAIN_IMAGES_DIR);
                            handWriteRecognizer.trainFromMat((Mat[]) res.get("images"), (int[]) res.get("labels"), HandWriteRecognizer.getSvmModelFilePath(appContext));

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
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, appContext, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }


    }

    @Test
    public void TestPredictWithSvmModelFile() {
        final Context appContext = InstrumentationRegistry.getInstrumentation().getContext();
        final String base64FormatStr ="data:image/png;base64,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";

        BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(appContext) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                        try {
                            FileOperator optr = new FileOperator(appContext);
                            optr.moveSvmModelFromAssetsDirToFilesDir(appContext);
                            HandWriteRecognizer handWriteRecognizer = new HandWriteRecognizer();
                            handWriteRecognizer.recognizeMultipleBase64FormatImg(base64FormatStr,appContext);
//                            Log.d("InstrumentTest","predict result is --->"+result );
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
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, appContext, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}
