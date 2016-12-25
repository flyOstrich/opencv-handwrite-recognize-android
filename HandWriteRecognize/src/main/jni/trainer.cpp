#include "include/trainer.h"

std::vector<cv::Mat> Trainer::HogComputer::getGradientList(std::vector<cv::Mat> &image_list) {
    cv::HOGDescriptor hog (
             cvSize(28, 28)    //winSize
            , cvSize(14, 14)  //表示块（block）大小
            , cvSize(7, 7)    //块滑动增量（blockStride）大小
            , cvSize(7, 7)    //cvSize(4, 3)表示胞元（cell）大小
            , 9);
    cv::Mat gray;
    std::vector<cv::Mat> gradient_lst;
    std::vector< cv::Point > location;
    std::vector< float > descriptors;

    std::vector< cv::Mat >::const_iterator img = image_list.begin();
    std::vector< cv::Mat >::const_iterator end = image_list.end();
    for( ; img != end ; ++img )
    {
        cvtColor( *img, gray, cv::COLOR_BGR2GRAY );
        hog.compute( gray, descriptors, cv::Size( 28, 28 ),cv::Size( 0, 0 ) ,location);
        gradient_lst.push_back(cv::Mat( descriptors ).clone() );
    }
    return gradient_lst;
}

std::vector<cv::Mat> Trainer::ImageLoader::loadImages(std::vector<std::string> image_path_list,std::string dir) {
    std::vector<cv::Mat> image_list;
    while (!image_path_list.empty()){
        const std::string img_file_name=dir+"/"+image_path_list.back();
        image_list.push_back(cv::imread(img_file_name));
        image_path_list.pop_back();
    }
    return image_list;
}