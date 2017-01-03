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

        static const int COLOR_WHITE=1;

        //去除图像四周空白的部分
        static void removeEmptySpace(cv::Mat &src, cv::Mat dst);

        static void thinning(cv::InputArray input, cv::OutputArray output);

        //设置图片的背景和前景色
        static void swapBgAndFgColor(cv::Mat &src, cv::Mat dst,int bgColor);
    };

}