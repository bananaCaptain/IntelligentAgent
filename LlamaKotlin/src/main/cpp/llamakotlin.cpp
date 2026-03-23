#include <android/log.h>
#include <jni.h>
#include <string>

#include "llama.h"

#define LOG_TAG "LlamaKotlin"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static llama_model *g_model = nullptr;
static llama_context *g_context = nullptr;

extern "C"
JNIEXPORT jstring JNICALL
Java_com_plantain_llamakotlin_NativeLib_stringFromJNI(JNIEnv *env, jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_plantain_llamakotlin_NativeLib_initBackend(JNIEnv * /*env*/, jobject /*this*/) {
    llama_backend_init();
    LOGI("llama backend initialized");
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_plantain_llamakotlin_NativeLib_systemInfo(JNIEnv *env, jobject /*this*/) {
    return env->NewStringUTF(llama_print_system_info());
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_plantain_llamakotlin_NativeLib_loadModel(JNIEnv *env, jobject /*this*/, jstring modelPath) {
    const char *path = env->GetStringUTFChars(modelPath, nullptr);
    LOGI("load model: %s", path);

    if (g_model) {
        llama_model_free(g_model);
        g_model = nullptr;
    }

    llama_model_params model_params = llama_model_default_params();
    g_model = llama_model_load_from_file(path, model_params);
    env->ReleaseStringUTFChars(modelPath, path);

    return g_model ? 0 : 1;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_plantain_llamakotlin_NativeLib_prepareContext(JNIEnv * /*env*/, jobject /*this*/, jint nCtx) {
    if (!g_model) {
        LOGE("model is null");
        return 1;
    }

    if (g_context) {
        llama_free(g_context);
        g_context = nullptr;
    }

    llama_context_params ctx_params = llama_context_default_params();
    ctx_params.n_ctx = nCtx;
    g_context = llama_init_from_model(g_model, ctx_params);
    return g_context ? 0 : 2;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_plantain_llamakotlin_NativeLib_chat(JNIEnv *env, jobject /*this*/, jstring prompt) {
    const char *text = env->GetStringUTFChars(prompt, nullptr);
    std::string result = std::string("LLama stub reply: ") + text;
    env->ReleaseStringUTFChars(prompt, text);
    return env->NewStringUTF(result.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_plantain_llamakotlin_NativeLib_release(JNIEnv * /*env*/, jobject /*this*/) {
    if (g_context) {
        llama_free(g_context);
        g_context = nullptr;
    }
    if (g_model) {
        llama_model_free(g_model);
        g_model = nullptr;
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_plantain_llamakotlin_NativeLib_shutdown(JNIEnv * /*env*/, jobject /*this*/) {
    llama_backend_free();
}
