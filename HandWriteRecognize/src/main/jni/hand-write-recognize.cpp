#include <jni.h>
#include <string>
#include "include/trainer.h"
#include "include/log.h"
#include "include/utils.h"

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
jstring
Java_com_allere_handwriterecognize_HandWriteRecognizer_recognize(
        JNIEnv *env,
        jobject,
        jstring recognizing_image_path,
        jobjectArray  recognizing_image_files,
        jstring  svm_model_path) {

    std::string s_recognizing_image_path=Util::ParamConverter::convertJstringToString(env,recognizing_image_path);
    std::string s_svm_model_path=Util::ParamConverter::convertJstringToString(env,svm_model_path);
    std::vector<std::string> v_recognizing_image_files=Util::ParamConverter::convertJobjectArrayToVector(env,recognizing_image_files);



    Ptr<SVM> svm= StatModel::load<SVM>( s_svm_model_path.c_str() );
    LOGD("the predict result is:");
    while(!v_recognizing_image_files.empty()){
        std::string recognizing_img_file=v_recognizing_image_files.back();
        std::string recognizing_img_url=s_recognizing_image_path+"/"+recognizing_img_file;
        Mat descriptorMat=Trainer::HogComputer::getHogDescriptorMat(recognizing_img_url.c_str());
        int recognize_result=svm->predict(descriptorMat);
        LOGD("%s---->%d",recognizing_img_url.c_str(),recognize_result);
        v_recognizing_image_files.pop_back();
    }


    return env->NewStringUTF("success");
}


extern "C"
jstring
Java_com_allere_handwriterecognize_HandWriteRecognizer_train(
        JNIEnv *env,
        jobject,
        jobjectArray joarr,
        jstring  jpath,
        jstring  svm_model_path) {
    LOGD("Java_com_allere_handwriterecognize_HandWriteRecognizer_train");

    Trainer::ImageLoader img_loader;
    Trainer::HogComputer hogComputer;

    std::string dir=Util::ParamConverter::convertJstringToString(env,jpath);
    std::string trained_result_location=Util::ParamConverter::convertJstringToString(env,svm_model_path);
    std::vector<std::string> img_path_vector=Util::ParamConverter::convertJobjectArrayToVector(env,joarr);

    std::list< std::pair<int,cv::Mat> > img_list=img_loader.loadImages(img_path_vector,dir);
    std::list< std::pair<int,cv::Mat> > gradient_list=Trainer::HogComputer::getGradientList(img_list);
    std::pair<cv::Mat,cv::Mat> train_data=Trainer::HogComputer::convertGradientToMlFormat(gradient_list);
    hogComputer.trainSvm(train_data,trained_result_location);
    return env->NewStringUTF("success");
}
extern "C"
jstring
Java_com_allere_handwriterecognize_HandWriteRecognizer_trainFromMat(
        JNIEnv *env,
        jobject,
        jobjectArray matList,
        jintArray labels,
        jstring  svm_model_path) {
    Trainer::HogComputer hogComputer;
    std::list<std::pair<int,cv::Mat> > imgList=Util::ParamConverter::convertJobjectArrayToMatVector(env,matList,labels);
    std::string trained_result_location=Util::ParamConverter::convertJstringToString(env,svm_model_path);
    std::list< std::pair<int,cv::Mat> > gradient_list=Trainer::HogComputer::getGradientList(imgList);
    std::pair<cv::Mat,cv::Mat> train_data=Trainer::HogComputer::convertGradientToMlFormat(gradient_list);
    hogComputer.trainSvm(train_data,trained_result_location);
    LOGD("train file location--->%s",trained_result_location.c_str());
    return env->NewStringUTF("success");
}





