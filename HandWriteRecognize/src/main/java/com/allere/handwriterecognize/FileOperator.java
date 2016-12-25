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
    private static final String TRAIN_IMAGES_DIR = "trainImages";
    private Context ctx=null;

    public FileOperator(Context context) {
        this.setCtx(context);
    }

    public String[] getTrainFileNames() throws IOException {
        return this.getCtx().getAssets().list(TRAIN_IMAGES_DIR);
    }

    public boolean createTrainImageDir() throws Exception{
        Context ctx=this.getCtx();
        if(ctx==null) throw new Exception("can not find appContext");
        String dataFilePath=ctx.getFilesDir().getAbsolutePath();
        String trainImageDir=dataFilePath+"/"+TRAIN_IMAGES_DIR;
        File file=new File(trainImageDir);
        if(!file.exists()){
            file.mkdir();
            return false;
        }
        return true;
    }

    public void MoveTrainFilesToFileDir(String[] trainFiles) throws Exception {
        InputStream srcfile;
        FileOutputStream desFile;
        this.createTrainImageDir();
        for (int i = 0; i < trainFiles.length; i++) {
            String fileName = trainFiles[i];
            srcfile = this.getCtx().getAssets().open(TRAIN_IMAGES_DIR + "/" + fileName);
            int dataLen = srcfile.available();
            byte[] buffer = new byte[dataLen];
            srcfile.read(buffer);
            srcfile.close();
            desFile = new FileOutputStream(this.getCtx().getFilesDir().getAbsolutePath()
                    + "/"
                    + TRAIN_IMAGES_DIR
                    + "/"
                    + fileName);
            desFile.write(buffer);
            desFile.close();
        }
    }

    public String getTrainImageDir(){
       return this.getCtx().getFilesDir().getAbsolutePath()+"/"+TRAIN_IMAGES_DIR;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }
}
