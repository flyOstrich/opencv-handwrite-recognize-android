package com.allere.handwriterecognize;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by allere on 16/12/23.
 */

public class FileOperator {
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
