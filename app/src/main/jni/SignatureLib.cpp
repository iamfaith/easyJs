
#include <jni.h>
#include <string.h>
#include <android/log.h>
#include <malloc.h>

#define  LOG_TAG    "native-log"
//#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define  Log(...)   if(LOG_ENABLE == 1) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

const char *APP_SIGNATURE = "0BF97AE625BF2D7FC2BA965C9836EC18";

void ByteToHexStr(const char *source, char *dest, int sourceLen) {
    short i;
    char highByte, lowByte;

    for (i = 0; i < sourceLen; i++) {
        highByte = source[i] >> 4;
        lowByte = source[i] & 0x0f;
        highByte += 0x30;

        if (highByte > 0x39) {
            dest[i * 2] = highByte + 0x07;
        } else {
            dest[i * 2] = highByte;
        }

        lowByte += 0x30;
        if (lowByte > 0x39) {
            dest[i * 2 + 1] = lowByte + 0x07;
        } else {
            dest[i * 2 + 1] = lowByte;
        }
    }
}

//void Log(const char *fmt, ...) {
//    if (LOG_ENABLE == 1) {
//        va_list args;
//        va_start(args, fmt);
//        __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, args);
//        va_end(args);
//    }
//}

jstring ToMd5(JNIEnv *env, jbyteArray source) {
    // MessageDigest类
    jclass classMessageDigest = env->FindClass("java/security/MessageDigest");
    // MessageDigest.getInstance()静态方法
    jmethodID midGetInstance = env->GetStaticMethodID(classMessageDigest, "getInstance",
                                                      "(Ljava/lang/String;)Ljava/security/MessageDigest;");
    // MessageDigest object
    jobject objMessageDigest = env->CallStaticObjectMethod(classMessageDigest, midGetInstance,
                                                           env->NewStringUTF("md5"));

    // update方法，这个函数的返回值是void，写V
    jmethodID midUpdate = env->GetMethodID(classMessageDigest, "update", "([B)V");
    env->CallVoidMethod(objMessageDigest, midUpdate, source);

    // digest方法
    jmethodID midDigest = env->GetMethodID(classMessageDigest, "digest", "()[B");
    jbyteArray objArraySign = (jbyteArray) env->CallObjectMethod(objMessageDigest, midDigest);

    jsize intArrayLength = env->GetArrayLength(objArraySign);
    jbyte *byte_array_elements = env->GetByteArrayElements(objArraySign, NULL);
    size_t length = (size_t) intArrayLength * 2 + 1;
    char *char_result = (char *) malloc(length);
    memset(char_result, 0, length);

    // 将byte数组转换成16进制字符串，发现这里不用强转，jbyte和unsigned char应该字节数是一样的
    ByteToHexStr((const char *) byte_array_elements, char_result, intArrayLength);
    // 在末尾补\0
    *(char_result + intArrayLength * 2) = '\0';

    jstring stringResult = env->NewStringUTF(char_result);
    // release
    env->ReleaseByteArrayElements(objArraySign, byte_array_elements, JNI_ABORT);
    // 释放指针使用free
    free(char_result);
    env->DeleteLocalRef(classMessageDigest);
    env->DeleteLocalRef(objMessageDigest);

    return stringResult;
}

JNIEXPORT jstring JNICALL
Java_lzj_com_jnitest_MainActivity_loadSignature(JNIEnv *env, jobject context) {
    // 获得Context类
    jclass cls = env->GetObjectClass(context);
    // 得到getPackageManager方法的ID
    jmethodID mid = env->GetMethodID(cls, "getPackageManager",
                                     "()Landroid/content/pm/PackageManager;");

    // 获得应用包的管理器
    jobject pm = env->CallObjectMethod(context, mid);

    // 得到getPackageName方法的ID
    mid = env->GetMethodID(cls, "getPackageName", "()Ljava/lang/String;");
    // 获得当前应用包名
    jstring packageName = (jstring) env->CallObjectMethod(context, mid);

    // 获得PackageManager类
    cls = env->GetObjectClass(pm);
    // 得到getPackageInfo方法的ID
    mid = env->GetMethodID(cls, "getPackageInfo",
                           "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    // 获得应用包的信息
    jobject packageInfo = env->CallObjectMethod(pm, mid, packageName, 0x40); //GET_SIGNATURES = 64;
    // 获得PackageInfo 类
    cls = env->GetObjectClass(packageInfo);
    // 获得签名数组属性的ID
    jfieldID fid = env->GetFieldID(cls, "signatures", "[Landroid/content/pm/Signature;");
    // 得到签名数组
    jobjectArray signatures = (jobjectArray) env->GetObjectField(packageInfo, fid);
    // 得到签名
    jobject signature = env->GetObjectArrayElement(signatures, 0);

    // 获得Signature类
    cls = env->GetObjectClass(signature);
    // 得到toCharsString方法的ID
    mid = env->GetMethodID(cls, "toByteArray", "()[B");
    // 返回当前应用签名信息
    jbyteArray signatureByteArray = (jbyteArray) env->CallObjectMethod(signature, mid);

    return ToMd5(env, signatureByteArray);
}

jboolean checkSignature(
        JNIEnv *env, jobject context) {

    jstring appSignature = Java_lzj_com_jnitest_MainActivity_loadSignature(env,
                                                                           context); // 当前 App 的签名

    jstring releaseSignature = env->NewStringUTF(APP_SIGNATURE); // 发布时候的签名
    const char *charAppSignature = env->GetStringUTFChars(appSignature, NULL);
    const char *charReleaseSignature = env->GetStringUTFChars(releaseSignature, NULL);

    Log("app signature: %s", charAppSignature);
    Log("release signature: %s", charReleaseSignature);

    jboolean result = JNI_FALSE;
    // 比较是否相等
    if (charAppSignature != NULL && charReleaseSignature != NULL) {
        if (strcmp(charAppSignature, charReleaseSignature) == 0) {
            result = JNI_TRUE;
        }
    }

    env->ReleaseStringUTFChars(appSignature, charAppSignature);
    env->ReleaseStringUTFChars(releaseSignature, charReleaseSignature);

    return result;
}

static jobject getApplication(JNIEnv *env) {
    jobject application = NULL;
    jclass activity_thread_clz = env->FindClass("android/app/ActivityThread");
    if (activity_thread_clz != NULL) {
        jmethodID currentApplication = env->GetStaticMethodID(
                activity_thread_clz, "currentApplication", "()Landroid/app/Application;");
        if (currentApplication != NULL) {
            application = env->CallStaticObjectMethod(activity_thread_clz, currentApplication);
        } else {
            //           LOGE("Cannot find method: currentApplication() in ActivityThread.");
        }
        env->DeleteLocalRef(activity_thread_clz);
    } else {
//        LOGE("Cannot find class: android.app.ActivityThread");
    }

    return application;
}

/**
 * 检查加载该so的应用的签名，与预置的签名是否一致
 */
static jboolean checkSignature(JNIEnv *env) {

    // 调用 getContext 方法得到 Context 对象
    jobject appContext = getApplication(env);

    if (appContext != NULL) {
        jboolean signatureValid = checkSignature(
                env, appContext);
        return signatureValid;
    }

    return JNI_FALSE;
}

/**
 * 加载 so 文件的时候，会触发 OnLoad
 * 检测失败，返回 -1，App 就会 Crash
 */
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void **) (&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    Log("start checkSignature log(%d) release(%d)", LOG_ENABLE, RELEASE_MODE);
    if (RELEASE_MODE == 1) {
        if (checkSignature(env) != JNI_TRUE) {
//        LOGI("  checkSignature = false ");
            // 检测不通过，返回 -1 就会使 App crash
            return -1;
        }
    }
    return JNI_VERSION_1_6;
}
