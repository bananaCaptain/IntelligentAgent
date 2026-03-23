#include <android/log.h>
#include <jni.h>
#include <string>
#include <vector>
#include <sstream>

#include "llama.h"

#define LOG_TAG "LlamaKotlin"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static llama_model *g_model = nullptr;
static llama_context *g_context = nullptr;
static llama_sampler *g_sampler = nullptr;
static llama_batch g_batch;

// Helper function to add a token to a batch
static void batch_add(struct llama_batch & batch, llama_token id, llama_pos pos, const std::vector<llama_seq_id> & seq_ids, bool logits) {
    batch.token   [batch.n_tokens] = id;
    batch.pos     [batch.n_tokens] = pos;
    batch.n_seq_id[batch.n_tokens] = seq_ids.size();
    for (size_t i = 0; i < seq_ids.size(); ++i) {
        batch.seq_id[batch.n_tokens][i] = seq_ids[i];
    }
    batch.logits  [batch.n_tokens] = logits;
    batch.n_tokens++;
}

// Helper to get string from jstring
static std::string jstring2string(JNIEnv *env, jstring jStr) {
    if (!jStr) return "";
    const char *chars = env->GetStringUTFChars(jStr, NULL);
    std::string ret(chars);
    env->ReleaseStringUTFChars(jStr, chars);
    return ret;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_plantain_llamakotlin_LlamaKotlin_stringFromJNI(JNIEnv *env, jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_plantain_llamakotlin_LlamaKotlin_initBackend(JNIEnv * /*env*/, jobject /*this*/) {
    llama_backend_init();
    LOGI("llama backend initialized");
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_plantain_llamakotlin_LlamaKotlin_systemInfo(JNIEnv *env, jobject /*this*/) {
    return env->NewStringUTF(llama_print_system_info());
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_plantain_llamakotlin_LlamaKotlin_loadModel(JNIEnv *env, jobject /*this*/, jstring modelPath) {
    std::string path = jstring2string(env, modelPath);
    LOGI("load model: %s", path.c_str());

    if (g_model) {
        llama_model_free(g_model);
        g_model = nullptr;
    }

    llama_model_params model_params = llama_model_default_params();
    g_model = llama_model_load_from_file(path.c_str(), model_params);
    
    if (!g_model) {
        LOGE("failed to load model");
        return 1;
    }
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_plantain_llamakotlin_LlamaKotlin_prepareContext(JNIEnv * /*env*/, jobject /*this*/, jint nCtx) {
    if (!g_model) {
        LOGE("model is null");
        return 1;
    }

    if (g_context) {
        llama_free(g_context);
        g_context = nullptr;
    }
    
    if (g_sampler) {
        llama_sampler_free(g_sampler);
        g_sampler = nullptr;
    }

    llama_context_params ctx_params = llama_context_default_params();
    ctx_params.n_ctx = nCtx > 0 ? nCtx : 2048;
    g_context = llama_init_from_model(g_model, ctx_params);
    
    if (!g_context) {
        LOGE("failed to init context");
        return 2;
    }
    
    g_batch = llama_batch_init(512, 0, 1);
    
    // Create a default sampler chain
    llama_sampler_chain_params sampler_params = llama_sampler_chain_default_params();
    g_sampler = llama_sampler_chain_init(sampler_params);
    llama_sampler_chain_add(g_sampler, llama_sampler_init_top_k(40));
    llama_sampler_chain_add(g_sampler, llama_sampler_init_top_p(0.9f, 1));
    llama_sampler_chain_add(g_sampler, llama_sampler_init_temp(0.7f));
    llama_sampler_chain_add(g_sampler, llama_sampler_init_dist(1234)); // seed

    return 0;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_plantain_llamakotlin_LlamaKotlin_chat(JNIEnv *env, jobject /*this*/, jstring prompt) {
    if (!g_model || !g_context) {
        LOGE("Model or context not loaded");
        return env->NewStringUTF("Error: model or context not loaded");
    }

    std::string text = jstring2string(env, prompt);
    LOGI("Received prompt: %s", text.c_str());

    const struct llama_vocab * vocab = llama_model_get_vocab(g_model);

    // Tokenize prompt
    std::vector<llama_token> tokens;
    tokens.resize(text.length() + 4); // roughly allocate
    
    int n_tokens = llama_tokenize(vocab, text.c_str(), text.length(), tokens.data(), tokens.size(), true, true);
    if (n_tokens < 0) {
        // resize and try again
        tokens.resize(-n_tokens);
        n_tokens = llama_tokenize(vocab, text.c_str(), text.length(), tokens.data(), tokens.size(), true, true);
        if (n_tokens < 0) {
            LOGE("Failed to tokenize prompt");
            return env->NewStringUTF("Error: tokenization failed");
        }
    }
    tokens.resize(n_tokens);

    // Evaluate prompt tokens
    g_batch.n_tokens = 0;
    for (int i = 0; i < tokens.size(); i++) {
        batch_add(g_batch, tokens[i], i, {0}, false);
    }
    // Only output logits for the last token
    g_batch.logits[g_batch.n_tokens - 1] = true;

    if (llama_decode(g_context, g_batch) != 0) {
        LOGE("llama_decode failed");
        return env->NewStringUTF("Error: llama_decode failed");
    }

    std::stringstream response;
    int n_cur = tokens.size();
    int n_decode = 0;
    int max_predict = 128; // Simple limit for now

    // Generation loop
    while (n_decode < max_predict) {
        llama_token new_token_id = llama_sampler_sample(g_sampler, g_context, g_batch.n_tokens - 1);
        llama_sampler_accept(g_sampler, new_token_id);

        // Check for EOS
        if (llama_vocab_is_eog(vocab, new_token_id)) {
            break;
        }

        // Convert token to string
        char buf[128];
        int n_chars = llama_token_to_piece(vocab, new_token_id, buf, sizeof(buf), 0, true);
        if (n_chars > 0) {
            response << std::string(buf, n_chars);
        }

        // Prepare next batch
        g_batch.n_tokens = 0;
        batch_add(g_batch, new_token_id, n_cur, {0}, true);
        
        if (llama_decode(g_context, g_batch) != 0) {
            LOGE("llama_decode failed during generation");
            break;
        }

        n_cur += 1;
        n_decode += 1;
    }

    LOGI("Generation complete. Length: %d", n_decode);
    return env->NewStringUTF(response.str().c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_plantain_llamakotlin_LlamaKotlin_release(JNIEnv * /*env*/, jobject /*this*/) {
    if (g_sampler) {
        llama_sampler_free(g_sampler);
        g_sampler = nullptr;
    }
    llama_batch_free(g_batch);
    
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
Java_com_plantain_llamakotlin_LlamaKotlin_shutdown(JNIEnv * /*env*/, jobject /*this*/) {
    llama_backend_free();
}