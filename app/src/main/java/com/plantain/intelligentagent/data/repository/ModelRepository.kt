package com.plantain.intelligentagent.data.repository

import android.util.Log
import com.plantain.intelligentagent.data.model.QwenRequest
import com.plantain.intelligentagent.data.model.QwenResponse
import com.plantain.intelligentagent.data.model.ZaiRequest
import com.plantain.intelligentagent.data.model.ZaiResponse
import com.plantain.intelligentagent.data.aidl.IntelligentServiceDataSource
import com.plantain.intelligentagent.data.remote.QwenDataSource
import com.plantain.intelligentagent.data.remote.ZaiDataSource
import com.plantain.llamakotlin.LlamaKotlin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class ModelRepository(
    private val qwenDataSource: QwenDataSource,
    private val zaiDataSource: ZaiDataSource,
    private val intelligentServiceDataSource: IntelligentServiceDataSource,
    private val llamaKotlin: LlamaKotlin = LlamaKotlin()
) {

    private companion object {
        const val TAG = "ModelRepository"
    }

    init {
        // Initialize llama backend
        llamaKotlin.initBackend()
    }

    fun loadLocalModel(modelPath: String, nCtx: Int = 2048): Boolean {
        Log.d(TAG, "Loading local model: $modelPath")
        val loadResult = llamaKotlin.loadModel(modelPath)
        if (loadResult != 0) return false
        val ctxResult = llamaKotlin.prepareContext(nCtx)
        return ctxResult == 0
    }

    suspend fun chatLocal(prompt: String): String {
        Log.d(TAG, "Local chat(prompt=$prompt)")
        return withContext(Dispatchers.IO) {
            // Ensure llama operations run on IO dispatcher
            llamaKotlin.chat(prompt)
        }
    }

    val intelligentServiceBound: StateFlow<Boolean>
        get() = intelligentServiceDataSource.isBound

    suspend fun getServiceVerificationString(): String {
        Log.d(TAG, "Calling IntelligentService.getVerificationString()")
        return intelligentServiceDataSource.getVerificationString()
    }

    suspend fun getServiceLlamaSystemInfo(): String {
        Log.d(TAG, "Calling IntelligentService.getLlamaSystemInfo()")
        return intelligentServiceDataSource.getLlamaSystemInfo()

    }

    suspend fun loadLlamaModelViaService(modelPath: String, nCtx: Int = 2048): Int {
        Log.d(TAG, "Calling IntelligentService.loadLlamaModel(path=$modelPath, nCtx=$nCtx)")
        return intelligentServiceDataSource.loadLlamaModel(modelPath, nCtx)

    }

    suspend fun chatWithLlamaViaService(prompt: String): String {
        Log.d(TAG, "Calling IntelligentService.chatWithLlama(prompt=$prompt)")
        return intelligentServiceDataSource.chatWithLlama(prompt)

    }

    suspend fun chatQwen(prompt: String): QwenResponse {
        Log.d(TAG, "Qwen chat(prompt=$prompt)")
        return qwenDataSource.chat(prompt)
    }

    suspend fun chatQwen(request: QwenRequest): QwenResponse {
        Log.d(TAG, "Qwen chat(request=$request)")
        return qwenDataSource.chat(request)
    }

    suspend fun chatZai(prompt: String): ZaiResponse {
        Log.d(TAG, "Zai chat(prompt=$prompt)")
        return zaiDataSource.chat(prompt)
    }

    suspend fun chatZai(request: ZaiRequest): ZaiResponse {
        Log.d(TAG, "Zai chat(request=$request)")
        return zaiDataSource.chat(request)
    }
}
