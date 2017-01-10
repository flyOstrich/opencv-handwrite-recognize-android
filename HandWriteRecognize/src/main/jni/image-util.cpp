#include <opencv2/opencv.hpp>
#include "include/image-util.h"
#include "include/log.h"
#include "include/type-util.h"
#include <iostream>

using namespace cv;
using namespace std;

int getColorCount(cv::Mat src, int bgColor) {
    int res = 0;
    for (int i = 0; i < src.rows; i++) {
        uchar *rowData = src.ptr<uchar>(i);
        for (int j = 0; j < src.cols; j++) {
            if(bgColor-50>rowData[j]||rowData[j]>bgColor+50)res++;
//            if (rowData[j] != bgColor)res++;
        }
    }
    return res;
}

void Util::ImageConverter::printMatrix(cv::Mat src) {
    for (int i = 0; i < src.rows; i++) {
        uchar *rowData = src.ptr<uchar>(i);
        std::string printRowStr = "";
        for (int j = 0; j < src.cols; j++) {
            int rgbVal = (int) rowData[j];
            std::string rgbStr = Util::TypeConverter::int2String(rgbVal);
            int strLen = std::strlen(rgbStr.c_str());
            if (strLen == 1) {
                printRowStr += rgbStr + "   ";
            }
            if (strLen == 2) {
                printRowStr += rgbStr + "  ";
            }
            if (strLen == 3) {
                printRowStr += rgbStr + " ";
            }
        }
        LOGD("%s", printRowStr.c_str());
    }
}

cv::Mat Util::ImageConverter::resize(cv::Mat &src, cv::Size size) {
    cv::Mat res;
    cv::resize(src, res, size, INTER_LANCZOS4);
    return res;
}

cv::Mat Util::ImageConverter::removeEmptySpace(cv::Mat &src) {
    Util::ImageConverter::printMatrix(src);
    int top = -1, bottom = -1, left = -1, right = -1;
    int step = 1;
    for (int i = 0; i < src.rows - step; i++) {
        cv::Mat tRange = src.rowRange(i, i + step);
        cv::Mat bRange = src.rowRange(src.rows - step - i, src.rows - i);
        if (top == -1 && getColorCount(tRange, 255) > 0) {
            top = i;
        }
        if (bottom == -1 && getColorCount(bRange, 255) > 0) {
            bottom = src.rows - i;
        }
        if (top != -1 && bottom != -1)break;
    }

    for (int i = 0; i < src.cols - step; i++) {
        cv::Mat lRange = src.colRange(i, i + step);
        cv::Mat rRange = src.colRange(src.cols - step - i, src.cols - i);
        if (left == -1 && getColorCount(lRange, 255) > 0) {
            left = i;
        }
        if (right == -1 && getColorCount(rRange, 255) > 0) {
            right = src.cols - i;
        }
        if (left != -1 && right != -1)break;
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
    cv::Mat dst(Size(src.cols, src.rows), CV_8UC1);
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


std::list<std::list<cv::Mat> > Util::ImageConverter::cutImage(cv::Mat src) {
    int row = src.rows;
    int col = src.cols;
    const int step = 5;
    //横向剪切,得到每一行
    int currentRow = 0;
    bool startFound = false;
    cv::Mat stepMat;
    std::list<std::list<cv::Mat> > list;
    int bgColor = Util::ImageConverter::getImageBgColor(src);
    int start;
    int end;
    while (currentRow < row - step) {
        stepMat = src.rowRange(currentRow, currentRow + step);
        int colorCount = getColorCount(stepMat, bgColor);
        if (startFound && colorCount == 0) {
            startFound = false;
            end = currentRow + step;
            cv::Mat rowMat = src.rowRange(start, end);
            //纵向剪切，得到每一个文字
            int currentCol = 0;
            bool vStartFound = false;
            int vStart;
            int vEnd;
            cv::Mat vStepMat;
            std::list<cv::Mat> vList;
            while (currentCol < col - step) {
                vStepMat = rowMat.colRange(currentCol, currentCol + step);
                int vColorCount = getColorCount(vStepMat, bgColor);
                if (vStartFound && vColorCount == 0) {
                    vStartFound = false;
                    vEnd = currentCol + step;
                    vList.push_front(rowMat.colRange(vStart, vEnd));
                }
                if (!vStartFound && vColorCount > 0) {
                    vStartFound = true;
                    vStart = currentCol;
                }
                currentCol++;
            }
            list.push_front(vList);
        }
        if (!startFound && colorCount > 0) {
            startFound = true;
            start = currentRow;
        }
        currentRow++;
    }
    return list;
}

int Util::ImageConverter::getImageBgColor(cv::Mat src) {
    int blackCnt = 0;
    int whiteCnt = 0;
    for (int i = 0; i < src.rows; i++) {
        uchar *rowData = src.ptr<uchar>(i);
        for (int j = 0; j < src.cols; j++) {
            int rgbVal = (int) rowData[j];
            if (rgbVal < 30) {
                blackCnt++;
            }
            if (rgbVal > 230) {
                whiteCnt++;
            }
        }
    }
    return blackCnt > whiteCnt ? ImageConverter::COLOR_BLACK : ImageConverter::COLOR_WHITE;
}

cv::Mat Util::ImageConverter::cannyImage(cv::Mat src) {
    cv::Mat dst(cv::Size(src.cols, src.rows), CV_8UC1);
    LOGD("before cvCanny");
    cv::Canny(src, dst, 50, 75, 3);
//    cvCanny(src, &dst, 50, 75, 3);
    return dst;
}

void cvHilditchThin1(cv::Mat &src, cv::Mat &dst) {
    //http://cgm.cs.mcgill.ca/~godfried/teaching/projects97/azar/skeleton.html#algorithm
    //算法有问题，得不到想要的效果
    if (src.type() != CV_8UC1) {
        printf("只能处理二值或灰度图像\n");
        return;
    }
    //非原地操作时候，copy src到dst
    if (dst.data != src.data) {
        src.copyTo(dst);
    }

    int i, j;
    int width, height;
    //之所以减2，是方便处理8邻域，防止越界
    width = src.cols - 2;
    height = src.rows - 2;
    int step = src.step;
    int p2, p3, p4, p5, p6, p7, p8, p9;
    uchar *img;
    bool ifEnd;
    int A1;
    cv::Mat tmpimg;
    while (1) {
        dst.copyTo(tmpimg);
        ifEnd = false;
        img = tmpimg.data + step;
        for (i = 2; i < height; i++) {
            img += step;
            for (j = 2; j < width; j++) {
                uchar *p = img + j;
                A1 = 0;
                if (p[0] > 0) {
                    if (p[-step] == 0 && p[-step + 1] > 0) //p2,p3 01模式
                    {
                        A1++;
                    }
                    if (p[-step + 1] == 0 && p[1] > 0) //p3,p4 01模式
                    {
                        A1++;
                    }
                    if (p[1] == 0 && p[step + 1] > 0) //p4,p5 01模式
                    {
                        A1++;
                    }
                    if (p[step + 1] == 0 && p[step] > 0) //p5,p6 01模式
                    {
                        A1++;
                    }
                    if (p[step] == 0 && p[step - 1] > 0) //p6,p7 01模式
                    {
                        A1++;
                    }
                    if (p[step - 1] == 0 && p[-1] > 0) //p7,p8 01模式
                    {
                        A1++;
                    }
                    if (p[-1] == 0 && p[-step - 1] > 0) //p8,p9 01模式
                    {
                        A1++;
                    }
                    if (p[-step - 1] == 0 && p[-step] > 0) //p9,p2 01模式
                    {
                        A1++;
                    }
                    p2 = p[-step] > 0 ? 1 : 0;
                    p3 = p[-step + 1] > 0 ? 1 : 0;
                    p4 = p[1] > 0 ? 1 : 0;
                    p5 = p[step + 1] > 0 ? 1 : 0;
                    p6 = p[step] > 0 ? 1 : 0;
                    p7 = p[step - 1] > 0 ? 1 : 0;
                    p8 = p[-1] > 0 ? 1 : 0;
                    p9 = p[-step - 1] > 0 ? 1 : 0;
                    //计算AP2,AP4
                    int A2, A4;
                    A2 = 0;
                    //if(p[-step]>0)
                    {
                        if (p[-2 * step] == 0 && p[-2 * step + 1] > 0) A2++;
                        if (p[-2 * step + 1] == 0 && p[-step + 1] > 0) A2++;
                        if (p[-step + 1] == 0 && p[1] > 0) A2++;
                        if (p[1] == 0 && p[0] > 0) A2++;
                        if (p[0] == 0 && p[-1] > 0) A2++;
                        if (p[-1] == 0 && p[-step - 1] > 0) A2++;
                        if (p[-step - 1] == 0 && p[-2 * step - 1] > 0) A2++;
                        if (p[-2 * step - 1] == 0 && p[-2 * step] > 0) A2++;
                    }


                    A4 = 0;
                    //if(p[1]>0)
                    {
                        if (p[-step + 1] == 0 && p[-step + 2] > 0) A4++;
                        if (p[-step + 2] == 0 && p[2] > 0) A4++;
                        if (p[2] == 0 && p[step + 2] > 0) A4++;
                        if (p[step + 2] == 0 && p[step + 1] > 0) A4++;
                        if (p[step + 1] == 0 && p[step] > 0) A4++;
                        if (p[step] == 0 && p[0] > 0) A4++;
                        if (p[0] == 0 && p[-step] > 0) A4++;
                        if (p[-step] == 0 && p[-step + 1] > 0) A4++;
                    }


                    //printf("p2=%d p3=%d p4=%d p5=%d p6=%d p7=%d p8=%d p9=%d\n", p2, p3, p4, p5, p6,p7, p8, p9);
                    //printf("A1=%d A2=%d A4=%d\n", A1, A2, A4);
                    if ((p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9) > 1 &&
                        (p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9) < 7 && A1 == 1) {
                        if (((p2 == 0 || p4 == 0 || p8 == 0) || A2 != 1) &&
                            ((p2 == 0 || p4 == 0 || p6 == 0) || A4 != 1)) {
                            dst.at<uchar>(i, j) = 0; //满足删除条件，设置当前像素为0
                            ifEnd = true;
                            //printf("\n");

                            //PrintMat(dst);
                        }
                    }
                }
            }
        }
        //printf("\n");
        //PrintMat(dst);
        //PrintMat(dst);
        //已经没有可以细化的像素了，则退出迭代
        if (!ifEnd) break;
    }
}

cv::Mat Util::ImageConverter::thinImage(cv::Mat src) {
    cv::Mat dst;
    cvHilditchThin1(src, dst);
    return dst;
    const int maxIterations = 1000;
    assert(src.type() == CV_8UC1);
//    cv::Mat dst;
    int width = src.cols;
    int height = src.rows;
    src.copyTo(dst);
    int count = 0;  //记录迭代次数
    while (true) {
        count++;
        cout << "迭代次数：" << endl << count << endl;
        if (maxIterations != -1 && count > maxIterations) //限制次数并且迭代次数到达
        {
            break;
        }
        std::vector<uchar *> mFlag; //用于标记需要删除的点

        //对点标记
        for (int i = 0; i < height; ++i) {
            uchar *p = dst.ptr<uchar>(i);
            for (int j = 0; j < width; ++j) {
                //如果满足四个条件，进行标记
                //  p9 p2 p3
                //  p8 p1 p4
                //  p7 p6 p5
                uchar p1 = p[j];
                if (p1 != 1) continue;
                uchar p4 = (j == width - 1) ? 0 : *(p + j + 1);
                uchar p8 = (j == 0) ? 0 : *(p + j - 1);
                uchar p2 = (i == 0) ? 0 : *(p - dst.step + j);
                uchar p3 = (i == 0 || j == width - 1) ? 0 : *(p - dst.step + j + 1);
                uchar p9 = (i == 0 || j == 0) ? 0 : *(p - dst.step + j - 1);
                uchar p6 = (i == height - 1) ? 0 : *(p + dst.step + j);
                uchar p5 = (i == height - 1 || j == width - 1) ? 0 : *(p + dst.step + j + 1);
                uchar p7 = (i == height - 1 || j == 0) ? 0 : *(p + dst.step + j - 1);


                if ((p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9) != 1) {

                    int ap = 0;
                    ap = abs(p3 - p2) + abs(p4 - p3) + abs(p5 - p4) + abs(p6 - p5) + abs(p7 - p6) +
                         abs(p8 - p7) + abs(p9 - p8) + abs(p2 - p9);

                    if (ap == 0 || ap == 2 || ap == 4) {

                        if (p2 * p4 * p6 == 0 && p4 * p2 * p8 == 0) {
                            if (ap == 4) {
                                if ((p4 * p6 == 1 && p3 + p7 > 0 && p2 == 0 && p9 == 0 && p8 == 0 &&
                                     p5 == 0) ||
                                    (p2 * p4 == 1 && p5 + p9 > 0 && p6 == 0 && p7 == 0 && p8 == 0 &&
                                     p3 == 0)) {

                                    //标记
                                    mFlag.push_back(p + j);
                                }
                            }
                            else
                                mFlag.push_back(p + j);
                        }
                    }
                }
            }
        }


        //将标记的点删除
        for (std::vector<uchar *>::iterator i = mFlag.begin(); i != mFlag.end(); ++i) {
            **i = 0;
        }


        //直到没有点满足，算法结束
        if (mFlag.empty()) {
            break;
        }
        else {
            mFlag.clear();//将mFlag清空
        }



        //对点标记
        for (int i = 0; i < height; ++i) {
            uchar *p = dst.ptr<uchar>(i);
            for (int j = 0; j < width; ++j) {
                //如果满足四个条件，进行标记
                //  p9 p2 p3
                //  p8 p1 p4
                //  p7 p6 p5
                uchar p1 = p[j];
                if (p1 != 1) continue;
                uchar p4 = (j == width - 1) ? 0 : *(p + j + 1);
                uchar p8 = (j == 0) ? 0 : *(p + j - 1);
                uchar p2 = (i == 0) ? 0 : *(p - dst.step + j);
                uchar p3 = (i == 0 || j == width - 1) ? 0 : *(p - dst.step + j + 1);
                uchar p9 = (i == 0 || j == 0) ? 0 : *(p - dst.step + j - 1);
                uchar p6 = (i == height - 1) ? 0 : *(p + dst.step + j);
                uchar p5 = (i == height - 1 || j == width - 1) ? 0 : *(p + dst.step + j + 1);
                uchar p7 = (i == height - 1 || j == 0) ? 0 : *(p + dst.step + j - 1);


                if ((p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9) != 1) {
                    int ap = 0;

                    ap = abs(p3 - p2) + abs(p4 - p3) + abs(p5 - p4) + abs(p6 - p5) + abs(p7 - p6) +
                         abs(p8 - p7) + abs(p9 - p8) + abs(p2 - p9);


                    if (ap == 0 || ap == 2 || ap == 4) {

                        if (p2 * p6 * p8 == 0 && p4 * p6 * p8 == 0) {
                            if (ap == 4) {
                                if ((p2 * p8 == 1 && p3 + p7 > 0 && p4 == 0 && p9 == 0 && p6 == 0 &&
                                     p5 == 0) ||
                                    (p6 * p8 == 1 && p5 + p9 > 0 && p2 == 0 && p7 == 0 && p4 == 0 &&
                                     p3 == 0)) {

                                    //标记
                                    mFlag.push_back(p + j);
                                }
                            }
                            else
                                mFlag.push_back(p + j);
                        }
                    }
                }
            }
        }



        //将标记的点删除
        for (std::vector<uchar *>::iterator i = mFlag.begin(); i != mFlag.end(); ++i) {
            **i = 0;
        }


        //直到没有点满足，算法结束
        if (mFlag.empty()) {
            break;
        }
        else {
            mFlag.clear();//将mFlag清空
        }
    }

    return dst;
}


//二值化函数
cv::Mat Util::ImageConverter::twoValue(cv::Mat src, int t, bool swapColor) {
    float s = 0;
    Mat srcimg = src.clone();
    for (int i = 0; i < srcimg.rows; i++) {
        uchar *p = srcimg.ptr<uchar>(i);    //获取行地址
        for (int j = 0; j < srcimg.cols; j++) {
            s = (float) srcimg.at<uchar>(i, j);
            if (s < (float) t)
                p[j] = swapColor ? 255 : 0;           //修改灰度值
            else
                p[j] = swapColor ? 0 : 255;
        }

    }
    return srcimg;

}

cv::Mat Util::ImageConverter::dilate(cv::Mat src) {
    Mat res = src.clone();
    Mat element = getStructuringElement(MORPH_CROSS, Size(2, 2));
    cv::dilate(src, res, element);
    return res;
}



