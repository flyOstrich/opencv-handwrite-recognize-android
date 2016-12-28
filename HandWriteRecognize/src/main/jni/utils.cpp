#include "include/utils.h";

std::vector<std::string> Util::ParamConverter::convertJobjectArrayToVector(JNIEnv *env,
                                                                           jobjectArray joarr) {
    std::vector<std::string> res_vector;
    int len = env->GetArrayLength(joarr);
    for (int i = 0; i < len; i++) {
        if(i==511){
            int b=1;
        }
        jstring j_str = (jstring) env->GetObjectArrayElement(joarr, i);
        const char *c_str = env->GetStringUTFChars(j_str, 0);
        std::string str = c_str;
        env->ReleaseStringUTFChars(j_str,c_str);
        env->DeleteLocalRef(j_str);
        res_vector.push_back(str);
    }
    return res_vector;
}

std::string Util::ParamConverter::convertJstringToString(JNIEnv *env, jstring jstr) {
    const char* c_path_str=env->GetStringUTFChars(jstr,0);
    std::string str=c_path_str;
    env->ReleaseStringUTFChars(jstr,c_path_str);
    return str;
}