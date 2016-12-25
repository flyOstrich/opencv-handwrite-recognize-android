#pragma once
#include <vector>
#include <iostream>
#include <jni.h>
#include <opencv2/opencv.hpp>
//using namespace cv;
//using namespace std;
namespace Trainer {
    class HogComputer {
    public:
       std::vector<cv::Mat> getGradientList(std::vector<cv::Mat> & image_list);
    };
    class ImageLoader {
    public:
        std::vector<cv::Mat> loadImages(std::vector<std::string> image_path_list,std::string dir);
    };
}