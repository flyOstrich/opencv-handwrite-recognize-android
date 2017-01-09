#include "include/trainer.h"
#include "include/log.h"
#include "include/image-util.h"
#include <fstream>

using namespace cv;
using namespace cv::ml;
using namespace std;

std::list<std::pair<int, cv::Mat> > Trainer::HogComputer::getGradientList(
        std::list<std::pair<int, cv::Mat> > &image_list) {

    cv::Mat gray;
    std::list<std::pair<int, cv::Mat> > gradient_lst;
    while (!image_list.empty()) {
        std::pair<int, cv::Mat> img_pair = image_list.front();
        cv::Mat img = img_pair.second;
        int img_label = img_pair.first;
        cvtColor(img, gray, cv::COLOR_BGR2GRAY);
        LOGD("train image");
        Util::ImageConverter::printMatrix(gray);
        gradient_lst.push_back(std::pair<int, cv::Mat>(img_label
                , Trainer::HogComputer::getHogDescriptorForImage(gray)));
        image_list.pop_front();
    }
    return gradient_lst;
}

std::pair<cv::Mat, cv::Mat>  Trainer::HogComputer::convertGradientToMlFormat(
        std::list<std::pair<int, cv::Mat> > &gradient_list) {

    //--Convert data
    const int rows = (int) gradient_list.size();
    const int cols = gradient_list.front().second.cols;
    cv::Mat trainData = cv::Mat(rows, cols, CV_32FC1);
    int i = 0;
    std::vector<int> labels;
    while (!gradient_list.empty()) {
        std::pair<int, cv::Mat> gradient_pair = gradient_list.front();
        gradient_pair.second.copyTo(trainData.row(i));
        labels.push_back(gradient_pair.first);
        i++;
        gradient_list.pop_front();
    }

    return std::pair<cv::Mat, cv::Mat>(cv::Mat(labels, CV_32FC1), trainData);
}

void Trainer::HogComputer::trainSvm(std::pair<cv::Mat, cv::Mat> train_data,
                                    std::string trained_result_location) {
    LOGD("method trainSvm");
    cv::Ptr<cv::ml::SVM> svm = cv::ml::SVM::create();
    cv::Mat train_data_mat = train_data.second;
    cv::Mat train_data_labels = train_data.first;
    /* Default values to train SVM */
//    svm->setTermCriteria(cv::TermCriteria( CV_TERMCRIT_ITER+CV_TERMCRIT_EPS, 1000, 1e-3 ));
    svm->setKernel(cv::ml::SVM::LINEAR  );
    svm->setType(
            cv::ml::SVM::C_SVC); // C_SVC; // EPSILON_SVR; // may be also NU_SVR; // do regression task
    svm->train(train_data_mat, cv::ml::ROW_SAMPLE, train_data_labels);
    LOGD("method trainSvm save %s",trained_result_location.c_str());
    svm->save(trained_result_location);
    int p;
    for (int i = 0; i < train_data_labels.rows; i++) {
        cv::Mat test_row = train_data_mat.row(i);
        p = svm->predict(test_row);
        LOGD("predict result:%d --> %d", i, p);
    }
    ifstream labelCharacterData(trained_result_location.c_str());
    string buf;
    string lrl = "";
    while (labelCharacterData) {
        if (getline(labelCharacterData, buf)) {
            lrl += buf + "\n";
        }
    }
    int b = 2;

}

std::list<std::pair<int, cv::Mat> > Trainer::ImageLoader::loadImages(
        std::vector<std::string> image_path_list, std::string dir) {
    std::list<std::pair<int, cv::Mat> > image_list;
    while (!image_path_list.empty()) {
        std::string img_file_name = image_path_list.back();
        const std::string img_file_loc = dir + "/" + image_path_list.back();
        int pos = img_file_name.find_first_of("_");
        std::string label = img_file_name.substr(0, pos);
        int iLabel = atoi(label.c_str());
        this->imageLabels.push_back(iLabel);
        image_list.push_back(std::pair<int, cv::Mat>(iLabel, cv::imread(img_file_loc)));

        image_path_list.pop_back();
    }
    return image_list;
}

cv::Mat Trainer::HogComputer::getHogDescriptorMat(const char *recognizing_img_path) {
    Mat gray, resizedGray;
    Mat recognizing_img = imread(recognizing_img_path);
    cvtColor(recognizing_img, gray, cv::COLOR_BGR2GRAY);
    resize(gray, resizedGray,TRAIN_IMAGE_SIZE);
    return Trainer::HogComputer::getHogDescriptorForImage(resizedGray);
}
cv::Mat Trainer::HogComputer::getHogDescriptorMat(cv::Mat mat) {
    Mat gray, resizedGray;
    cvtColor(mat, gray, cv::COLOR_BGR2GRAY);
    resize(gray, resizedGray, TRAIN_IMAGE_SIZE);
    return Trainer::HogComputer::getHogDescriptorForImage(resizedGray);
}

cv::Mat Trainer::HogComputer::getHogDescriptorForImage(cv::Mat image) {
    cv::HOGDescriptor hog(
            TRAIN_IMAGE_SIZE    //winSize
            , cvSize(14, 14)  //表示块（block）大小
            , cvSize(7, 7)    //块滑动增量（blockStride）大小
            , cvSize(7, 7)    //cvSize(4, 3)表示胞元（cell）大小
            , 9);
    std::vector<float> descriptors;
    std::vector<cv::Point> location;
    hog.compute(image, descriptors,TRAIN_IMAGE_SIZE, cv::Size(0, 0), location);
    int size=descriptors.size();
    Mat dMat = cv::Mat(descriptors).clone();
    Mat resMat(1,size,CV_32FC1);
    cv::transpose(dMat,resMat);
    return resMat;
}