#include <jni.h>
#include <string>
#include "trainer.h"
#include "log.h"
#include "param-util.h"
#include "image-util.h"

using namespace cv;
using namespace cv::ml;

extern "C"
jint
Java_com_allere_handwriterecognize_HandWriteRecognizer_recognize(
        JNIEnv *env,
        jobject,
        jlong jl_recognizing_image,
        jstring svm_model_path) {
    std::string s_svm_model_path = Util::ParamConverter::convertJstringToString(env,
                                                                                svm_model_path);
    cv::Mat recognizing_image = *(cv::Mat *) jl_recognizing_image;
    //将图片灰度化
    cv::Mat gray(cv::Size(recognizing_image.cols,recognizing_image.rows),CV_8UC1);
    cvtColor(recognizing_image, gray, cv::COLOR_BGR2GRAY);
    LOGD("将图片灰度化(width:%d,height:%d)",gray.cols,gray.rows);
    //改变背景色
    int color=Util::ImageConverter::COLOR_WHITE;
    cv::Mat swap=Util::ImageConverter::swapBgAndFgColor(gray, color);
    LOGD("改变背景色(width:%d,height:%d，color:%d)",swap.cols,swap.rows,color);
    //截切图片空白部分
//    cv::Mat noEmptyRes=Util::ImageConverter::removeEmptySpace(swap);
//    LOGD("截切图片空白部分(width:%d,height:%d)",noEmptyRes.cols,noEmptyRes.rows);
    //获取图片中的每个字的子区域图片
    std::list<std::list<cv::Mat> > characterImageMatrix=Util::ImageConverter::cutImage(swap);
    LOGD("获取图片中的每个字的子区域图片 row:%d",characterImageMatrix.size());
    //缩放图片
    cv::Mat resizedRes=Util::ImageConverter::resize(swap,cv::Size(28,28));
    LOGD("缩放图片(width:%d,height:%d)",resizedRes.cols,resizedRes.rows);
    //提取图片骨架
    cv::Mat thinRes=Util::ImageConverter::thinning(resizedRes);
    LOGD("提取图片骨架(width:%d,height:%d)",thinRes.cols,thinRes.rows);
    Util::ImageConverter::printMatrix(thinRes);

    //识别
    Ptr<SVM> svm = StatModel::load<SVM>(s_svm_model_path.c_str());
    Mat descriptorMat = Trainer::HogComputer::getHogDescriptorForImage(thinRes);
    int recognize_result = svm->predict(descriptorMat);
    LOGD("predict result is ----> %d", recognize_result);
    return recognize_result;
}


extern "C"
jstring
Java_com_allere_handwriterecognize_HandWriteRecognizer_train(
        JNIEnv *env,
        jobject,
        jobjectArray joarr,
        jstring jpath,
        jstring svm_model_path) {
    LOGD("Java_com_allere_handwriterecognize_HandWriteRecognizer_train");

    Trainer::ImageLoader img_loader;
    Trainer::HogComputer hogComputer;

    std::string dir = Util::ParamConverter::convertJstringToString(env, jpath);
    std::string trained_result_location = Util::ParamConverter::convertJstringToString(env,
                                                                                       svm_model_path);
    std::vector<std::string> img_path_vector = Util::ParamConverter::convertJobjectArrayToVector(
            env, joarr);

    std::list<std::pair<int, cv::Mat> > img_list = img_loader.loadImages(img_path_vector, dir);
    std::list<std::pair<int, cv::Mat> > gradient_list = Trainer::HogComputer::getGradientList(
            img_list);
    std::pair<cv::Mat, cv::Mat> train_data = Trainer::HogComputer::convertGradientToMlFormat(
            gradient_list);
    hogComputer.trainSvm(train_data, trained_result_location);
    return env->NewStringUTF("success");
}
extern "C"
jstring
Java_com_allere_handwriterecognize_HandWriteRecognizer_trainFromMat(
        JNIEnv *env,
        jobject,
        jobjectArray matList,
        jintArray labels,
        jstring svm_model_path) {
    Trainer::HogComputer hogComputer;
    LOGD("method trainFromMat");
    std::list<std::pair<int, cv::Mat> > imgList = Util::ParamConverter::convertJobjectArrayToMatVector(
            env, matList, labels);
    LOGD("method imgList");
    std::string trained_result_location = Util::ParamConverter::convertJstringToString(env,
                                                                                       svm_model_path);
    LOGD("method trained_result_location");

    std::list<std::pair<int, cv::Mat> > gradient_list = Trainer::HogComputer::getGradientList(
            imgList);
    LOGD("method gradient_list");

    std::pair<cv::Mat, cv::Mat> train_data = Trainer::HogComputer::convertGradientToMlFormat(
            gradient_list);
    LOGD("method train_data");
    hogComputer.trainSvm(train_data, trained_result_location);
    LOGD("train file location--->%s", trained_result_location.c_str());
    return env->NewStringUTF("success");
}

extern "C"
void
Java_com_allere_handwriterecognize_HandWriteRecognizer_testImageOperate(
        JNIEnv *env,
        jobject,
        jlong mat_address,
        jstring img_save_location) {
    cv::Mat gray, resizedGray;
    cv::Mat img_mat = *(cv::Mat *) mat_address;
    std::string s_img_save_location = Util::ParamConverter::convertJstringToString(env,
                                                                                   img_save_location);
    cvtColor(img_mat, gray, cv::COLOR_BGR2GRAY);
    cv::Mat swap(cv::Size(gray.rows, gray.cols), CV_8UC1);
//    Util::ImageConverter::swapBgAndFgColor(gray, swap, Util::ImageConverter::COLOR_BLACK);
    cv::Mat noEmptyRes=Util::ImageConverter::removeEmptySpace(swap);
    cv::Mat resizeRes=Util::ImageConverter::resize(noEmptyRes,cv::Size(28,28));
//    Util::ImageConverter::thinning(cutRes,thinRes);


    LOGD("img save location ---->%s", s_img_save_location.c_str());
//    imwrite(s_img_save_location.c_str(), thinRes);
}


//保存训练图片
extern "C"
void
Java_com_allere_handwriterecognize_HandWriteRecognizer_saveTrainImage(
        JNIEnv *env,
        jobject,
        jlong mat_address,
        jstring img_save_location) {
    LOGD("Java_com_allere_handwriterecognize_HandWriteRecognizer_saveTrainImage");
    cv::Mat gray;
    cv::Mat img_mat = *(cv::Mat *) mat_address;
    std::string s_img_save_location = Util::ParamConverter::convertJstringToString(env,
                                                                                   img_save_location);
    LOGD("Train Image Origin:img rows--->%d,img cols--->%d",img_mat.rows,img_mat.cols);
    cvtColor(img_mat, gray, cv::COLOR_BGR2GRAY);
    LOGD("Gray Image Matrix:");
    Util::ImageConverter::printMatrix(gray);
    LOGD("Gray Image :img rows--->%d,img cols--->%d",gray.rows,gray.cols);
    cv::Mat swap=Util::ImageConverter::swapBgAndFgColor(gray, Util::ImageConverter::COLOR_BLACK);
    LOGD("Swap Image : rows--->%d,img cols--->%d",swap.rows,swap.cols);
    cv::Mat noEmptyRes=Util::ImageConverter::removeEmptySpace(swap);
    LOGD("Cut Image : rows--->%d,img cols--->%d",noEmptyRes.rows,noEmptyRes.cols);
    cv::Mat resizedRes=Util::ImageConverter::resize(noEmptyRes,cv::Size(28,28));
    cv::Mat thinRes=Util::ImageConverter::thinning(resizedRes);
    LOGD("img save location ---->%s", s_img_save_location.c_str());
    imwrite(s_img_save_location.c_str(), thinRes);
}








