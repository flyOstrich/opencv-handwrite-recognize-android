#pragma once

#include <iostream>
#include <vector>
#include <jni.h>

namespace Util {
    class ParamConverter{
    public:
        static std::vector<std::string> convertJobjectArrayToVector(JNIEnv *env,jobjectArray joarr);
        static std::string convertJstringToString(JNIEnv *env,jstring jstr);
    };
}