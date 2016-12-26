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
       cv::Mat convertGradientToMlFormat(std::vector<cv::Mat> & gradient_list);
       void trainSvm(cv::Mat train_data,std::vector<int> labels,std::string trained_result_location);
    };
    class ImageLoader {
    public:
        std::vector<cv::Mat> loadImages(std::vector<std::string> image_path_list,std::string dir);
    };
}