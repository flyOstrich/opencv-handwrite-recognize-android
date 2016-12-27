#include <jni.h>
#include <string>
#include "include/trainer.h"
#include "include/log.h"

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
        jstring  svm_model_path) {
    Mat gray,resizedGray;

    const char* c_svm_model_path=env->GetStringUTFChars(svm_model_path,0);
    const char* c_recognizing_image_path=env->GetStringUTFChars(recognizing_image_path,0);
    Mat recognizing_image=imread(c_recognizing_image_path);
    cvtColor(recognizing_image, gray, cv::COLOR_BGR2GRAY );
    resize(gray,resizedGray,Size(28,28));
    Ptr<SVM> svm= StatModel::load<SVM>( c_svm_model_path );
    env->ReleaseStringUTFChars(svm_model_path,c_svm_model_path);
    env->ReleaseStringUTFChars(recognizing_image_path,c_recognizing_image_path);


    return env->NewStringUTF("success");
}


extern "C"
jstring
Java_com_allere_handwriterecognize_HandWriteRecognizer_train(
        JNIEnv *env,
        jobject,
        jobjectArray joarr,
        jstring  jpath) {
    LOGD("Java_com_allere_handwriterecognize_HandWriteRecognizer_train");

    Trainer::ImageLoader img_loader;
    Trainer::HogComputer hogComputer;
    int len = env->GetArrayLength(joarr);

    const char* c_path_str=env->GetStringUTFChars(jpath,0);
    std::string dir=c_path_str;
    env->ReleaseStringUTFChars(jpath,c_path_str);
    std::string trained_result_location=dir+"/handwrite_trained_result.yml";

    std::vector<std::string> img_path_vector;
    for (int i = 0; i < len; i++) {
        jstring j_str = (jstring) env->GetObjectArrayElement(joarr, i);
        const char *c_str = env->GetStringUTFChars(j_str, 0);
        std::string str = c_str;
        env->ReleaseStringUTFChars(j_str,c_str);
        img_path_vector.push_back(str);
    }
    std::list< std::pair<int,cv::Mat> > img_list=img_loader.loadImages(img_path_vector,dir);
    std::list< std::pair<int,cv::Mat> > gradient_list=Trainer::HogComputer::getGradientList(img_list);
    std::pair<cv::Mat,cv::Mat> train_data=Trainer::HogComputer::convertGradientToMlFormat(gradient_list);
    hogComputer.trainSvm(train_data,trained_result_location);
    return env->NewStringUTF("success");
}




