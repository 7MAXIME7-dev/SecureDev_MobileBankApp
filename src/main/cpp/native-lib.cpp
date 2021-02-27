#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring

JNICALL
Java_com_example_bankaccount_Keys_apiConfigKey(JNIEnv *env, jobject object) {
    std::string api_key = "https://60102f166c21e10017050128.mockapi.io/labbbank/config/";;
    return env->NewStringUTF(api_key.c_str());
}

extern "C" JNIEXPORT jstring
JNICALL
Java_com_example_bankaccount_Keys_apiAccountKey(JNIEnv *env, jobject object) {
    std::string api_key = "https://60102f166c21e10017050128.mockapi.io/labbbank/accounts/";;
    return env->NewStringUTF(api_key.c_str());
}

