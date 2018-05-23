#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring

JNICALL
Java_twigatatu_digitalmatatus_com_newtwigatatu_views_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
//extern "C"
//JNIEXPORT jstring JNICALL
//Java_twigatatu_digitalmatatus_com_newtwigatatu_views_MainActivity_stringFromJNI(JNIEnv *env,
//                                                                                jobject instance) {
//
//    // TODO
//
//
//    return env->NewStringUTF(returnValue);
//}