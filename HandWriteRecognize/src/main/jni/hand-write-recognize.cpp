#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>

//#include "./include/common.h"
//#include <android/asset_manager.h>
//#include <android/asset_manager_jni.h>
//#include <android/log.h>

#define TAG "HELLO"
//#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
//#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG , TAG, __VA_ARGS__)
//#define LOGI(...) __android_log_print(ANDROID_LOG_INFO , TAG, __VA_ARGS__)
//#define LOGW(...) __android_log_print(ANDROID_LOG_WARN , TAG, __VA_ARGS__)
//#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR , TAG, __VA_ARGS__)
//


using namespace cv;
using namespace cv::ml;


extern "C"
jstring
Java_com_allere_handwriterecognize_HandWriteRecognizer_recognize(
        JNIEnv *env,jlong imgMat,jint height,jint width) {

    std::string hello = "Hello from C++2222222222222";
    cv::Mat mat= *(cv::Mat *)imgMat;
    IplImage img=mat;
    const char* filename="HOG_SVM_DATA.xml";
//    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
//    AAsset* asset = AAssetManager_open(mgr, filename,AASSET_MODE_UNKNOWN);
//    off_t bufferSize = AAsset_getLength(asset);
//    char *buffer=(char *)malloc(bufferSize+1);
//    buffer[bufferSize]=0;
//    int numBytesRead = AAsset_read(asset, buffer, bufferSize);

    return env->NewStringUTF(hello.c_str());
}

extern "C"
void
Java_com_allere_handwriterecognize_HandWriteRecognizer_testOpencv(){
    // Data for visual representation
    int width = 512, height = 512;
    Mat image = Mat::zeros(height, width, CV_8UC3);

    // Set up training data
    //! [setup1]
    int labels[4] = {1, -1, -1, -1};
    float trainingData[4][2] = { {501, 10}, {255, 10}, {501, 255}, {10, 501} };
    //! [setup1]
    //! [setup2]
    Mat trainingDataMat(4, 2, CV_32FC1, trainingData);
    Mat labelsMat(4, 1, CV_32SC1, labels);
    //! [setup2]


    // Train the SVM
    //! [init]
    Ptr<SVM> svm = SVM::create();
    svm->setType(SVM::C_SVC);
    svm->setKernel(SVM::LINEAR);
    svm->setTermCriteria(TermCriteria(TermCriteria::MAX_ITER, 100, 1e-6));
    //! [init]
    //! [train]
    svm->train(trainingDataMat, ROW_SAMPLE, labelsMat);
    //! [train]
    svm->save("/data/user/0/com.example.handwrite.test/files/trainres.yml");

}

