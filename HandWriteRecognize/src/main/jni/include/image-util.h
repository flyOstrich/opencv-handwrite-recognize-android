#pragma once

#include <iostream>
#include <vector>
#include <jni.h>
#include <cv.h>
#include <list>

namespace Util {
    class ImageConverter{
    public:
         static void thin(cv::Mat &src,cv::Mat dst,int iterations);
    };
}