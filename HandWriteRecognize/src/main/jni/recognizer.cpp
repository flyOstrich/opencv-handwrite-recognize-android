#include "recognizer.h"
#include "trainer.h"
#include "image-util.h"
#include "log.h"

list<pair<int, int> > Recognizer::recognizeRows(Mat &recognizingImg) {
    Mat resizedMat = Util::ImageConverter::resize(recognizingImg, TRAIN_IMAGE_SIZE);
    int imgHeight = resizedMat.rows;
    int scanStep = 10;
    Mat subImg;
    list<pair<int, int> > rowColorCntList;
    Util::ImageConverter::printMatrix(resizedMat);
    int bgColor=Util::ImageConverter::getImageBgColor(resizedMat);
    for (int i = 0; i < imgHeight - scanStep; i++) {
        subImg = resizedMat.rowRange(i, i+ scanStep);
        int colorCnt=Util::ImageConverter::getMatColorCount(subImg,bgColor);
        LOGD("colorCnt:%d",colorCnt);
        rowColorCntList.push_front(pair<int,int>(i, colorCnt));
    }
}