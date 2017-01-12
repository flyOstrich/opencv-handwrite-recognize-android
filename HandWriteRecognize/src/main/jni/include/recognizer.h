#pragma once

#include <iostream>
#include <list>
#include <opencv2/opencv.hpp>

using namespace std;
using namespace cv;

class Recognizer {
public:
    enum RECOGNIZE_STATUS {
        RECOGNIZE_START,
        RECOGNIZE_CONTINUE,
        RECOGNIZE_RESET
    };

    list<pair<int, int> > recognizeRows(Mat &recognizingImg);
};
