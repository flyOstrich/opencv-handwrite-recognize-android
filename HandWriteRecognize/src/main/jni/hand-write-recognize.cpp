#include <jni.h>
#include <iostream>
#include "trainer.h"
#include "log.h"
#include "param-util.h"
#include "image-util.h"
#include "type-util.h"
#include "recognizer.h"

using namespace cv;
using namespace cv::ml;
using namespace std;
using namespace Util;


Recognizer recognizer;
/**********************************************************************************
Func    Name: recognize
Descriptions: 将一张图片识别识别为单个文字结果
Input   para: jl_recognizing_image 图片地址
Input   para: svm_model_path       识别模型文件的地址
Return value: 整形识别结果
***********************************************************************************/
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
    cv::Mat gray(cv::Size(recognizing_image.cols, recognizing_image.rows), CV_8UC1);
    cvtColor(recognizing_image, gray, cv::COLOR_BGR2GRAY);
    LOGD("将图片灰度化(width:%d,height:%d)", gray.cols, gray.rows);
    //
    int color = Util::ImageConverter::COLOR_WHITE;
    cv::Mat swap = Util::ImageConverter::swapBgAndFgColor(gray, color);
    LOGD("改变背景色(width:%d,height:%d，color:%d)", swap.cols, swap.rows, color);
    //截切图片空白部分
    cv::Mat noEmptyRes = Util::ImageConverter::removeEmptySpace(swap);
    LOGD("截切图片空白部分(width:%d,height:%d)", noEmptyRes.cols, noEmptyRes.rows);
    //获取图片中的每个字的子区域图片
//    std::list<std::list<cv::Mat> > characterImageMatrix=Util::ImageConverter::cutImage(swap);
//    LOGD("获取图片中的每个字的子区域图片 row:%d",characterImageMatrix.size());
    //缩放图片
    cv::Mat resizedRes = Util::ImageConverter::resize(noEmptyRes, TRAIN_IMAGE_SIZE);
    LOGD("缩放图片(width:%d,height:%d)", resizedRes.cols, resizedRes.rows);
    //提取图片骨架
    cv::Mat thinRes = Util::ImageConverter::thinImage(resizedRes);
    LOGD("提取图片骨架(width:%d,height:%d)", thinRes.cols, thinRes.rows);
    Util::ImageConverter::printMatrix(thinRes);

    //识别
    Ptr<SVM> svm = StatModel::load<SVM>(s_svm_model_path.c_str());
    Mat descriptorMat = Trainer::HogComputer::getHogDescriptorForImage(thinRes);
    int recognize_result = svm->predict(descriptorMat);
    LOGD("predict result is ----> %d", recognize_result);
    return recognize_result;
}
/**********************************************************************************
Func    Name: recognize
Descriptions: 将一张图片按图片上的文字间隔进行分隔，并将分割后的图片进行识别，并将
识别结果以二维数组的形式返回，如：
     图片上的文字位置如下：

              1  2  3  4
                 5    6
              7 8   9
     则识别数组的结果为 :
      [["1","2","3","4"],["5","6"],["7","8","9"]]
Input   para: jl_recognizing_image 图片地址
Input   para: svm_model_path       识别模型文件的地址
Input   para: cutimg_save_dir      分割后图片的保存地址，如果没有，则不保存
Return value: 二维数组的识别结果
***********************************************************************************/
extern "C"
jobjectArray
Java_com_allere_handwriterecognize_HandWriteRecognizer_recognizeMulti(
        JNIEnv *env,
        jobject,
        jlong jl_recognizing_image,
        jstring svm_model_path,
        jstring cutimg_save_dir) {
    string s_svm_model_path = ParamConverter::convertJstringToString(env, svm_model_path);
    string s_cutimg_save_dir = ParamConverter::convertJstringToString(env, cutimg_save_dir);
    LOGD("s_svm_model_path %s", s_svm_model_path.c_str());
    LOGD("s_cutimg_save_dir %s", s_cutimg_save_dir.c_str());
    Mat recognizing_image = *(Mat *) jl_recognizing_image;
    //将图片灰度化

    Mat gray(Size(recognizing_image.cols, recognizing_image.rows), CV_8UC1);
    cvtColor(recognizing_image, gray, cv::COLOR_BGR2GRAY);
    LOGD("将图片灰度化(width:%d,height:%d)", gray.cols, gray.rows);
    //获取图片中的每个字的子区域图片(图片分割)
    list<list<Mat> > characterImageMatrix = ImageConverter::cutImage(gray);
    LOGD("获取图片中的每个字的子区域图片 row:%d", characterImageMatrix.size());
    //识别
    Ptr<SVM> svm = StatModel::load<SVM>(s_svm_model_path.c_str());
    jobjectArray rtObjArray = env->NewObjectArray(characterImageMatrix.size(), env->FindClass("[I"),
                                                  NULL);
    int idx = 0;
    while (!characterImageMatrix.empty()) {
        std::list<cv::Mat> rowImages = characterImageMatrix.back();
        int size = rowImages.size();
        jintArray rowRecognizeResAry = env->NewIntArray(size);
        jint rowRecognizeRes[size];
        int index = 0;
        while (!rowImages.empty()) {
            cv::Mat characterMat = rowImages.back();
            characterMat = Util::ImageConverter::removeEmptySpace(characterMat);
            characterMat = Util::ImageConverter::resize(characterMat, TRAIN_IMAGE_SIZE);
            LOGD("----Resized image to Recognize(width:%d,height:%d)----", characterMat.cols,
                 characterMat.rows);
            characterMat = Util::ImageConverter::twoValue(characterMat, 100, true);
            characterMat = Util::ImageConverter::thinImage(characterMat);
            Util::ImageConverter::printMatrix(characterMat);
            if (s_cutimg_save_dir != "NONE") {
                string cut_image = s_cutimg_save_dir + "/" + TypeConverter::int2String(idx)
                                   + TypeConverter::int2String(index) + "_img.bmp";
                LOGD("save cut img:%s", cut_image.c_str());
                imwrite(cut_image.c_str(), characterMat);
            }
            Mat descriptorMat = Trainer::HogComputer::getHogDescriptorForImage(characterMat);

            jint recognize_result = svm->predict(descriptorMat);
            LOGD("---------predict result %d------------", recognize_result);
            rowRecognizeRes[index] = recognize_result;
            rowImages.pop_back();
            index++;
        }
        env->SetIntArrayRegion(rowRecognizeResAry, 0, size, rowRecognizeRes);
        env->SetObjectArrayElement(rtObjArray, idx, rowRecognizeResAry);
        env->DeleteLocalRef(rowRecognizeResAry);
        idx++;
        characterImageMatrix.pop_back();
    }

//    cv::Mat mat1=Mat::zeros(1,100,CV_32F);
//    uchar* row1=mat1.ptr<uchar>(0);
//    row1[2]=5;
//    row1[4]=6;
//    row1[8]=5;
//    row1[18]=9;
//    row1[18]=9;
//    row1[18]=94;
//    row1[28]=93;
//    row1[68]=91;
//    Util::ImageConverter::printMatrix(mat1);
//    cv::Mat mat2=Mat::zeros(1,100,CV_32F);
//    Util::ImageConverter::printMatrix(mat2);
    cv::Mat img1 = cv::imread(
            "/storage/sdcard/Android/data/com.example.handwrite.test/files/testImages/00_img.bmp");
    cv::Mat img2 = cv::imread(
            "/storage/sdcard/Android/data/com.example.handwrite.test/files/testImages/01_img.bmp");
    cv::Mat dmat1 = Trainer::HogComputer::getHogDescriptorMat(img1);
    cv::Mat dmat2 = Trainer::HogComputer::getHogDescriptorMat(img2);
    double val = cv::compareHist(dmat1, dmat2, HistCompMethods::HISTCMP_INTERSECT);
    LOGD("va is %f", val);


    return rtObjArray;
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
    cv::Mat noEmptyRes = Util::ImageConverter::removeEmptySpace(gray);
    cv::Mat resizeRes = Util::ImageConverter::resize(noEmptyRes, TRAIN_IMAGE_SIZE);
    cv::Mat thinRes = Util::ImageConverter::thinImage(resizeRes);
    recognizer.recognizeRows(thinRes);
    LOGD("img save location ---->%s", s_img_save_location.c_str());
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
    LOGD("Train Image Origin:img rows--->%d,img cols--->%d", img_mat.rows, img_mat.cols);
    cvtColor(img_mat, gray, cv::COLOR_BGR2GRAY);
    LOGD("Gray Image Matrix(width:%d,height:%d):", gray.cols, gray.rows);
    Util::ImageConverter::printMatrix(gray);
    cv::Mat noEmptyRes = Util::ImageConverter::removeEmptySpace(gray);
    LOGD("Cut Image : (width:%d,height:%d)", noEmptyRes.cols, noEmptyRes.rows);
    Util::ImageConverter::printMatrix(noEmptyRes);
    cv::Mat resizedRes = Util::ImageConverter::resize(noEmptyRes, TRAIN_IMAGE_SIZE);
    LOGD("Resize Image : (width:%d,height:%d)", resizedRes.cols, resizedRes.rows);
    Util::ImageConverter::printMatrix(resizedRes);
    cv::Mat thinRes = Util::ImageConverter::thinImage(resizedRes);
    LOGD("Thin Image : (width:%d,height:%d)", thinRes.cols, thinRes.rows);
    Util::ImageConverter::printMatrix(thinRes);
    cv::Mat twoValueRes = Util::ImageConverter::twoValue(thinRes, 100, true);
    cv::Mat res = Util::ImageConverter::thinImage(twoValueRes);
    LOGD("TwoValue Image : (width:%d,height:%d)", twoValueRes.cols, twoValueRes.rows);
    Util::ImageConverter::printMatrix(res);
    LOGD("img save location ---->%s", s_img_save_location.c_str());
    imwrite(s_img_save_location.c_str(), res);
}








