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

extern "C"
jstring
Java_com_allere_handwrite_HandWriteRecognize_recognize(
        JNIEnv *env,jlong,jlong) {
    std::string hello = "Hello from C++2222222222222";
    const char* filename="HOG_SVM_DATA.xml";
//    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
//    AAsset* asset = AAssetManager_open(mgr, filename,AASSET_MODE_UNKNOWN);
//    off_t bufferSize = AAsset_getLength(asset);
//    char *buffer=(char *)malloc(bufferSize+1);
//    buffer[bufferSize]=0;
//    int numBytesRead = AAsset_read(asset, buffer, bufferSize);

    return env->NewStringUTF(hello.c_str());
}

