package com.plantain.intelligentagent.data.repository

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

    suspend fun chat(prompt: String): QwenResponse {
        return qwenDataSource.chat(prompt)
    }

    suspend fun chat(request: QwenRequest): QwenResponse {
        return qwenDataSource.chat(request)
    }

    suspend fun chatZai(prompt: String): ZaiResponse {
        return zaiDataSource.chat(prompt)
    }

    suspend fun chatZai(request: ZaiRequest): ZaiResponse {
        return zaiDataSource.chat(request)
    }
}
