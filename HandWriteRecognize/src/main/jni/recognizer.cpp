#include "recognizer.h"
#include "trainer.h"
#include "image-util.h"
#include "log.h"

using namespace Util;
using namespace cv;

list<pair<int, int> > Recognizer::recognizeRows(Mat &recognizingImg) {
    Mat resizedMat = Util::ImageConverter::resize(recognizingImg, TRAIN_IMAGE_SIZE);
    int imgHeight = resizedMat.rows;
    int scanStep = 5;
    Mat subImg;
    list<pair<int, int> > rowColorCntList;
    Util::ImageConverter::printMatrix(resizedMat);
    int bgColor = Util::ImageConverter::getImageBgColor(resizedMat);
    for (int i = 0; i < imgHeight - scanStep; i++) {
        subImg = resizedMat.rowRange(i, i + scanStep);
        int colorCnt = Util::ImageConverter::getMatColorCount(subImg, bgColor);
        LOGD("colorCnt:%d", colorCnt);
        rowColorCntList.push_front(pair<int, int>(i, colorCnt));
    }
}

void Recognizer::pushStroke(Mat &stroke, string stroke_id) {
    Mat preProccessRes;
    preProccessRes = ImageConverter::getGrayImage(stroke);
    preProccessRes = ImageConverter::resize(stroke, RECOGNIZE_SIZE);
    preProccessRes = ImageConverter::thinImage(preProccessRes);
    int bgColor = ImageConverter::getImageBgColor(preProccessRes);
    Rect strokeBorder = ImageConverter::getImageBorderBox(preProccessRes, bgColor);
    Point centerPt=ImageConverter::getStrokeCenterPoint(preProccessRes,strokeBorder,bgColor);
    Stroke writingStroke;
    writingStroke.stroke_mat = preProccessRes;
    writingStroke.stroke_border = strokeBorder;
    writingStroke.stroke_id = stroke_id;
    writingStroke.strokeBgColor=bgColor;
    writingStroke.centerPt=centerPt;
    this->strokes.push_front(writingStroke);
}