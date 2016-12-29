package com.allere.handwriterecognize;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


import static org.opencv.android.Utils.bitmapToMat;


/**
 * Created by allere on 16/12/23.
 */

public class FileOperator {
    public static final String TAG="FileOperator";
    public static final String TRAIN_IMAGES_DIR = "t10k-images";
    public static final String TEST_IMAGES_DIR="testImages";
    private Context ctx=null;

    public FileOperator(Context context) {
        this.setCtx(context);
    }

    public String[] getAssetFileNames(String filesDir) throws IOException {
        return this.getCtx().getAssets().list(filesDir);
    }

    public boolean createDataImageDir(String filesDir) throws Exception{
        Context ctx=this.getCtx();
        if(ctx==null) throw new Exception("can not find appContext");
        String dataFilePath=ctx.getFilesDir().getAbsolutePath();
        String trainImageDir=dataFilePath+"/"+filesDir;
        File file=new File(trainImageDir);
        if(!file.exists()){
            file.mkdir();
            return false;
        }
        return true;
    }
    public Map ReadFilesMat(String[] files, String fileDir) throws IOException{
        InputStream srcfile;
        Map map=new HashMap();
        Mat[] matList=new Mat[files.length];
        int[] labels =new int[files.length];
        for(int i=0;i<files.length;i++){
            String fileName = files[i];
            int locEnd=fileName.indexOf("_");
            labels[i]=Integer.parseInt(fileName.substring(0,locEnd));
            srcfile = this.getCtx().getAssets().open(fileDir + "/" + fileName);
            int dataLen = srcfile.available();
            byte[] buffer = new byte[dataLen];
            srcfile.read(buffer);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = false;
            Bitmap img=BitmapFactory.decodeByteArray(buffer,0,buffer.length,opts);
            Mat mat = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC3);
            bitmapToMat(img, mat);
            matList[i]=mat;
            srcfile.close();
        }
        map.put("images",matList);
        map.put("labels",labels);
        Log.d(TAG,"reading image list done!--->"+matList);
        Log.d(TAG,"reading image list done!--->"+labels);

        return map;
    }

    public void MoveFilesToFileDir(String[] trainFiles,String fileDir) throws Exception {
        InputStream srcfile;
        FileOutputStream desFile;
        this.createDataImageDir(fileDir);
        for (int i = 0; i < trainFiles.length; i++) {
            String fileName = trainFiles[i];
            srcfile = this.getCtx().getAssets().open(fileDir + "/" + fileName);
            int dataLen = srcfile.available();
            byte[] buffer = new byte[dataLen];
            srcfile.read(buffer);
            srcfile.close();
            desFile = new FileOutputStream(this.getCtx().getFilesDir().getAbsolutePath()
                    + "/"
                    + fileDir
                    + "/"
                    + fileName);
            desFile.write(buffer);
            desFile.close();
            Log.d(TAG,"move img --> "+fileName);
        }
    }

    public String getDataImageDir(String fileDir){
       return this.getCtx().getFilesDir().getAbsolutePath()+"/"+fileDir;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }
}
