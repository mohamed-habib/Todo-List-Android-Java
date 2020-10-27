//
// Created by Mohamed Habib on 10/6/20.
//
#include <jni.h>


JNIEXPORT jstring JNICALL
Java_com_example_todolist_AddNewTask_getPasswordFromJni(JNIEnv *env, jobject instance){
return (*env)->NewStringUTF(env, "12345");
}



JNIEXPORT jstring JNICALL
Java_com_example_todolist_AddNewTask_getRandomKey(JNIEnv *env, jobject instance){
    return (*env)->NewStringUTF(env, "abc");
}




