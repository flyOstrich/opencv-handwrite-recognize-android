#include <jni.h>
#include <string>
#include "trainer.h"
#include "log.h"
#include "param-util.h"
#include "image-util.h"
//#include <android/asset_manager.h>
//#include <android/asset_manager_jni.h>




using namespace cv;
using namespace cv::ml;

//    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
//    AAsset* asset = AAssetManager_open(mgr, filename,AASSET_MODE_UNKNOWN);
//    off_t bufferSize = AAsset_getLength(asset);
//    char *buffer=(char *)malloc(bufferSize+1);
//    buffer[bufferSize]=0;
//    int numBytesRead = AAsset_read(asset, buffer, bufferSize);



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
    //改变背景色
    cv::Mat swap(cv::Size(gray.cols, gray.rows), CV_8UC1);
    Util::ImageConverter::swapBgAndFgColor(gray, swap, Util::ImageConverter::COLOR_BLACK);
    //截切图片空白部分
    cv::Mat cutRes(cv::Size(28,28),CV_8UC1);
    Util::ImageConverter::removeEmptySpace(swap, cutRes);
    LOGD("cutimg result rows-->%d,cols-->%d",cutRes.rows,cutRes.cols);
    //提取图片骨架
    cv::Mat thinRes(cv::Size(cutRes.cols,cutRes.rows),CV_8UC1);
    Util::ImageConverter::thinning(cutRes,thinRes);
    LOGD("thinning result rows-->%d,cols-->%d",thinRes.rows,thinRes.cols);
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
    Util::ImageConverter::swapBgAndFgColor(gray, swap, Util::ImageConverter::COLOR_BLACK);
    cv::Mat cutRes(cv::Size(28,28),CV_8UC1);
    Util::ImageConverter::removeEmptySpace(swap, cutRes);
    cv::Mat thinRes(cv::Size(cutRes.rows,cutRes.cols),CV_8UC1);
    Util::ImageConverter::thinning(cutRes,thinRes);


    LOGD("img save location ---->%s", s_img_save_location.c_str());
    imwrite(s_img_save_location.c_str(), thinRes);
}


//保存训练图片
extern "C"
void
Java_com_allere_handwriterecognize_HandWriteRecognizer_saveTrainImage(
        JNIEnv *env,
        jobject,
        jlong mat_address,
        jstring img_save_location) {
    cv::Mat gray;
    cv::Mat img_mat = *(cv::Mat *) mat_address;
    std::string s_img_save_location = Util::ParamConverter::convertJstringToString(env,
                                                                                   img_save_location);
    LOGD("img rows--->%d,img cols--->%d",img_mat.rows,img_mat.cols);
    cvtColor(img_mat, gray, cv::COLOR_BGR2GRAY);
    cv::Mat swap(cv::Size(gray.cols, gray.rows), CV_8UC1);
    Util::ImageConverter::swapBgAndFgColor(gray, swap, Util::ImageConverter::COLOR_BLACK);
    LOGD("swap img rows--->%d,img cols--->%d",swap.rows,swap.cols);
    cv::Size size(100,200);
    LOGD("size height--->%d,size width--->%d",size.height,size.width);

    cv::Mat cutRes(cv::Size(28,28),CV_8UC1);
    Util::ImageConverter::removeEmptySpace(swap, cutRes);
    cv::Mat thinRes(cv::Size(cutRes.cols,cutRes.rows),CV_8UC1);
    Util::ImageConverter::thinning(cutRes,thinRes);
    LOGD("img save location ---->%s", s_img_save_location.c_str());
    imwrite(s_img_save_location.c_str(), thinRes);
}








