//
// Created by allere on 16/12/7.
//
#pragma once

#include <iostream>
#include <cv.h>
#include <unistd.h>
#define FILE_PATH_MAX 80
#define LABEL_CHARACTER_MAP getProjectDir()+"/train/label_character_map.txt"
#define HOG_SVM_DATA getProjectDir()+"/HOG_SVM_DATA.xml"
#define HOG_TXT_DATA getProjectDir()+"/train/train-images/result.txt"
#define MOUSE_DRAW_IMG getProjectDir()+"/mouse_draw.jpg"


using namespace std;
using namespace cv;


//获取项目的根目录
inline string getProjectDir() {
    char *file_path;
    file_path = (char *) malloc(FILE_PATH_MAX);
    getcwd(file_path, FILE_PATH_MAX);
    string str = file_path;
    string rt;
    unsigned long len = str.length();
    for (unsigned long i = len; i > 0; i--) {
        char character = *(file_path + i);
        char separator[] = "/";
        if (character == *separator) {
            rt = str.substr(0, i);
            break;
        }
    }
    return rt;

}


