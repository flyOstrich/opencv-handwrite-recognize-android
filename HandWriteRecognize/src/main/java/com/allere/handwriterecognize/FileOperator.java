package com.allere.handwriterecognize;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by allere on 16/12/23.
 */

public class FileOperator {
    private static final String TRAIN_IMAGES_DIR = "trainImages";
    private Context ctx;

    public FileOperator(Context context) {
        this.setCtx(context);
    }

    public String[] getTrainFileNames() throws IOException {
        return this.getCtx().getAssets().list(TRAIN_IMAGES_DIR);
    }

    public void MoveTrainFilesToFileDir(String[] trainFiles) throws IOException {
        InputStream srcfile;
        FileOutputStream desFile;
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

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }
}
