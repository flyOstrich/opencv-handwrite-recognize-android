package com.example.handwrite;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * Created by allere on 16/12/15.
 */

public class ImagePro {
    public void imgget(){
        try{
            Mat image = Imgcodecs.imread("aaa");

        }catch (Exception e){
            System.out.print("aaaaaaaaaaaaa");

        }
    }
}
