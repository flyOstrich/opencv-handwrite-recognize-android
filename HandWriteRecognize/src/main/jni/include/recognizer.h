#pragma once

#include <iostream>
#include <list>
#include <opencv2/opencv.hpp>

#define RECOGNIZE_SIZE Size(1024,768)

using namespace std;
using namespace cv;
struct Stroke {
    string stroke_id;
    Rect stroke_border;
    Mat stroke_mat;
    Point centerPt;
    int strokeBgColor = -1;
};

class Recognizer {
public:
    enum RECOGNIZE_STATUS {
        RECOGNIZE_START,
        RECOGNIZE_CONTINUE,
        RECOGNIZE_RESET
    };
    list<Stroke> strokes;

    void pushStroke(Mat &stroke, string stroke_id);

    list<pair<int, int> > recognizeRows(Mat &recognizingImg);
};
