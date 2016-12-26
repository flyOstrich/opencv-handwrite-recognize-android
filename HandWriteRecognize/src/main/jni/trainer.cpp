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
cv::Mat Trainer::HogComputer::convertGradientToMlFormat(std::vector<cv::Mat> &gradient_list) {

    //--Convert data
    const int rows = (int)gradient_list.size();
    const int cols = (int)std::max( gradient_list[0].cols, gradient_list[0].rows );
    cv::Mat tmp(1, cols, CV_32FC1); //< used for transposition if needed
    cv::Mat trainData = cv::Mat(rows, cols, CV_32FC1 );
    std::vector< cv::Mat >::const_iterator itr = gradient_list.begin();
    std::vector< cv::Mat >::const_iterator end = gradient_list.end();
    for( int i = 0 ; itr != end ; ++itr, ++i )
    {
        CV_Assert( itr->cols == 1 ||
                   itr->rows == 1 );
        if( itr->cols == 1 )
        {
            cv::transpose( *(itr), tmp );
            tmp.copyTo( trainData.row( i ) );
        }
        else if( itr->rows == 1 )
        {
            itr->copyTo( trainData.row( i ) );
        }
    }
    return trainData;
}

void Trainer::HogComputer::trainSvm(cv::Mat train_data, std::vector<int> labels,std::string trained_result_location) {

    cv::Ptr<cv::ml::SVM> svm = cv::ml::SVM::create();
    /* Default values to train SVM */
    svm->setCoef0(0.0);
    svm->setDegree(3);
    svm->setTermCriteria(cv::TermCriteria( CV_TERMCRIT_ITER+CV_TERMCRIT_EPS, 1000, 1e-3 ));
    svm->setGamma(0);
    svm->setKernel(cv::ml::SVM::LINEAR);
    svm->setNu(0.5);
    svm->setP(0.1); // for EPSILON_SVR, epsilon in loss function?
    svm->setC(0.01); // From paper, soft classifier
    svm->setType(cv::ml::SVM::EPS_SVR); // C_SVC; // EPSILON_SVR; // may be also NU_SVR; // do regression task
    svm->train(train_data, cv::ml::ROW_SAMPLE, cv::Mat(labels));
    svm->save( trained_result_location);
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