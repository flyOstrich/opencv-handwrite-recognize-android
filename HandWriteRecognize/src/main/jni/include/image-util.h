#pragma once

#include <iostream>
#include <vector>
#include <jni.h>
#include <cv.h>
#include <list>

namespace Util {
    class ImageConverter {
    public:
        static const int COLOR_BLACK=0;

        static const int COLOR_WHITE=255;

        //去除图像四周空白的部分
        static cv::Mat removeEmptySpace(cv::Mat &src);

        //重新调整图片大小
        static cv::Mat resize(cv::Mat &src,cv::Size size);

        static cv::Mat thinning(cv::InputArray input);

        //设置图片的背景和前景色
        static cv::Mat swapBgAndFgColor(cv::Mat &src,int bgColor);

        //打印矩阵到控制台
        static void printMatrix(cv::Mat src);

        //将图片上的字进行行列剪切
        static std::list<std::list<cv::Mat> > cutImage(cv::Mat src);

        //获取图片背景色
        static int getImageBgColor(cv::Mat src);
    };

}