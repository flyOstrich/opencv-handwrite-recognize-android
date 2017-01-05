#include <opencv2/opencv.hpp>
#include "include/image-util.h"
#include "include/log.h";

using namespace cv;

int getColorCount(cv::Mat src, int color) {
    int res = 0;
    for (int i = 0; i < src.rows; i++) {
        uchar *rowData = src.ptr<uchar>(i);
        for (int j = 0; j < src.cols; j++) {
            if (rowData[j] == color)res++;
        }
    }
    return res;
}

void Util::ImageConverter::printMatrix(cv::Mat src) {
    for (int i = 0; i < src.rows; i++) {
        uchar *rowData = src.ptr<uchar>(i);
        std::string printRowStr = "";
        for (int j = 0; j < src.rows; j++) {
            if (rowData[j] > 0) {
                printRowStr += "1";
            } else {
                printRowStr += "0";
            }
        }
        LOGD("%s", printRowStr.c_str());
    }
}

cv::Mat Util::ImageConverter::resize(cv::Mat &src, cv::Size size) {
    cv::Mat res;
    cv::resize(src, res, size);
    return res;
}

cv::Mat Util::ImageConverter::removeEmptySpace(cv::Mat &src) {
    Util::ImageConverter::printMatrix(src);
    int top, bottom, left, right;
    int step = 1;
    bool firstFound = false;
    for (int i = 0; i < src.rows - step; i++) {
        cv::Mat range = src.rowRange(i, i + step);
        int count = getColorCount(range, 255);
        if (!firstFound && count > 0) {
            firstFound = true;
            top = i;
        }
        if (firstFound && count == 0) {
            bottom = i + step;
            break;
        }
    }

    firstFound = false;
    for (int j = 0; j < src.cols - step; j++) {
        cv::Mat range = src.colRange(j, j + step);
        int count = getColorCount(range, 255);
        if (!firstFound && count > 0) {
            firstFound = true;
            left = j;
        }
        if (firstFound && count == 0) {
            right = j + step;
            break;
        }
    }
    LOGD("Cut Image : top->%d , bottom->%d ,left->%d,right->%d", top, bottom, left, right);

    cv::Mat cutImg(cv::Size(bottom - top, right - left), CV_8UC1);

    if (bottom - top < 28) {
        bottom + 14 > src.rows ? bottom = src.rows : bottom += 14;
        top - 14 < 0 ? top = 0 : top -= 14;
    }
    if (right - left < 28) {
        left - 14 < 0 ? left = 0 : left -= 14;
        right + 14 > src.cols ? right = src.cols : right += 14;
    }
    LOGD("Cut Image : top->%d , bottom->%d ,left->%d,right->%d", top, bottom, left, right);

    cv::Mat temp = src.rowRange(top, bottom).colRange(left, right);
    temp.copyTo(cutImg);
    return cutImg;
}

cv::Mat Util::ImageConverter::swapBgAndFgColor(cv::Mat &src, int bgColor) {
    int height = src.rows;
    int width = src.cols;
    int blackCnt = 0, whiteCnt = 0;
    cv::Mat dst(Size(src.cols,src.rows),CV_8UC1);
    src.copyTo(dst);
    for (int i = 0; i < height; i++) {
        uchar *rowData = dst.ptr<uchar>(i);
        for (int j = 0; j < width; j++) {
            int colorVal = rowData[j];
            if (colorVal <= 100) {
                rowData[j] = 0;
                blackCnt++;
            }
            if (colorVal >= 150) {
                rowData[j] = 255;
                whiteCnt++;
            }
        }
    }
    if (blackCnt > whiteCnt) {
        if (bgColor == Util::ImageConverter::COLOR_BLACK)return dst;
        else {
            for (int i = 0; i < height; i++) {
                uchar *rowData = dst.ptr<uchar>(i);
                for (int j = 0; j < width; j++) {
                    int colorVal = rowData[j];
                    if (colorVal == 0) {
                        rowData[j] = 255;
                    }
                    if (colorVal == 255) {
                        rowData[j] = 0;
                    }
                }
            }
        }
    } else {
        if (bgColor == Util::ImageConverter::COLOR_WHITE)return dst;
        else {
            for (int i = 0; i < height; i++) {
                uchar *rowData = dst.ptr<uchar>(i);
                for (int j = 0; j < width; j++) {
                    int colorVal = rowData[j];
                    if (colorVal == 0) {
                        rowData[j] = 255;
                    }
                    if (colorVal == 255) {
                        rowData[j] = 0;
                    }
                }
            }
        }
    }
    return dst;
}

// Applies a thinning iteration to a binary image
void thinningIteration(Mat img, int iter) {
    Mat marker = Mat::zeros(img.size(), CV_8UC1);

    for (int i = 1; i < img.rows - 1; i++) {
        for (int j = 1; j < img.cols - 1; j++) {
            uchar p2 = img.at<uchar>(i - 1, j);
            uchar p3 = img.at<uchar>(i - 1, j + 1);
            uchar p4 = img.at<uchar>(i, j + 1);
            uchar p5 = img.at<uchar>(i + 1, j + 1);
            uchar p6 = img.at<uchar>(i + 1, j);
            uchar p7 = img.at<uchar>(i + 1, j - 1);
            uchar p8 = img.at<uchar>(i, j - 1);
            uchar p9 = img.at<uchar>(i - 1, j - 1);

            int A = (p2 == 0 && p3 == 1) + (p3 == 0 && p4 == 1) +
                    (p4 == 0 && p5 == 1) + (p5 == 0 && p6 == 1) +
                    (p6 == 0 && p7 == 1) + (p7 == 0 && p8 == 1) +
                    (p8 == 0 && p9 == 1) + (p9 == 0 && p2 == 1);
            int B = p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
            int m1 = iter == 0 ? (p2 * p4 * p6) : (p2 * p4 * p8);
            int m2 = iter == 0 ? (p4 * p6 * p8) : (p2 * p6 * p8);

            if (A == 1 && (B >= 2 && B <= 6) && m1 == 0 && m2 == 0)
                marker.at<uchar>(i, j) = 1;
        }
    }
    img &= ~marker;
}

// Apply the thinning procedure to a given image
cv::Mat Util::ImageConverter::thinning(const cv::_InputArray &input) {
    Mat processed = input.getMat().clone();
    // Enforce the range of the input image to be in between 0 - 255
    processed /= 255;

    Mat prev = Mat::zeros(processed.size(), CV_8UC1);
    Mat diff;

    do {
        thinningIteration(processed, 0);
        thinningIteration(processed, 1);
        absdiff(processed, prev, diff);
        processed.copyTo(prev);
    }
    while (countNonZero(diff) > 0);

    processed *= 255;
    return processed;
}



std::list<std::list<cv::Mat> > Util::ImageConverter::cutImage(cv::Mat src) {
    int row=src.rows;
    int col=src.cols;
    const int step=5;
    //横向剪切,得到每一行
    int currentRow=0;
    bool startFound=false;
    cv::Mat stepMat;
    std::list<std::list<cv::Mat> > list;
    int start;
    int end;
    while (currentRow<row-step){
        stepMat=src.rowRange(currentRow,currentRow+step);
        int colorCount=getColorCount(stepMat,Util::ImageConverter::COLOR_BLACK);
        if(startFound&&colorCount==0){
            startFound=false;
            end=currentRow+step;
            cv::Mat rowMat=src.rowRange(start,end);
            //纵向剪切，得到每一个文字
            int currentCol=0;
            bool vStartFound=false;
            int vStart;
            int vEnd;
            cv::Mat vStepMat;
            std::list<cv::Mat> vList;
            while (currentCol<col-step){
                vStepMat=rowMat.colRange(currentCol,currentCol+step);
                int vColorCount=getColorCount(vStepMat,Util::ImageConverter::COLOR_BLACK);
                if(vStartFound&&vColorCount==0){
                    vStartFound=false;
                    vEnd=currentCol+step;
                    vList.push_front(rowMat.colRange(vStart,vEnd));
                }
                if(!vStartFound&&vColorCount>0){
                    vStartFound=true;
                    vStart=currentCol;
                }
                currentCol++;
            }
            list.push_front(vList);
        }
        if(!startFound&&colorCount>0){
            startFound=true;
            start=currentRow;
        }
        currentRow++;
    }
    return list;
}


