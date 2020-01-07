//
// Created by amarthak on 07-01-2020.
//

#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_spawn_ai_utils_task_1utils_AppUtils_getAPICreds(JNIEnv *env, jobject /* this */) {
    std::string api_key = "onebotsolution:OneBotFinancialServices";
    return env->NewStringUTF(api_key.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_spawn_ai_utils_task_1utils_AppUtils_getUrl(JNIEnv *env, jobject /* this */) {
    std::string api_key = "https://api.spawnai.com/";
    return env->NewStringUTF(api_key.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_spawn_ai_utils_task_1utils_AppUtils_getESCreds(JNIEnv *env, jobject /* this */) {
    std::string api_key = "elastic:Spawn@#32";
    return env->NewStringUTF(api_key.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_spawn_ai_utils_task_1utils_AppUtils_getDataFile(JNIEnv *env, jobject) {
    std::string data_file = "bot_data.json";
    return env->NewStringUTF(data_file.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_spawn_ai_utils_task_1utils_AppUtils_getNewsUrl(JNIEnv *env, jobject) {
    std::string data_file = "https://api.spawnai.com/spawnai_file/news/news_data/";
    return env->NewStringUTF(data_file.c_str());
}