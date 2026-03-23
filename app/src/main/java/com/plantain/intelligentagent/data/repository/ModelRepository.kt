package com.plantain.intelligentagent.data.repository

import android.util.Log
import com.plantain.intelligentagent.data.model.QwenRequest
import com.plantain.intelligentagent.data.model.QwenResponse
import com.plantain.intelligentagent.data.model.ZaiRequest
import com.plantain.intelligentagent.data.model.ZaiResponse
import com.plantain.intelligentagent.data.remote.QwenDataSource
import com.plantain.intelligentagent.data.remote.ZaiDataSource

class ModelRepository(
    private val qwenDataSource: QwenDataSource,
    private val zaiDataSource: ZaiDataSource
) {

    private companion object {
        const val TAG = "ModelRepository"
    }

    suspend fun chat(prompt: String): QwenResponse {
        Log.d(TAG, "Qwen chat(prompt=$prompt)")
        return qwenDataSource.chat(prompt)
    }

    suspend fun chat(request: QwenRequest): QwenResponse {
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
