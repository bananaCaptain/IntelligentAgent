package com.plantain.intelligentagent.data.remote

import com.plantain.intelligentagent.data.model.QwenRequest
import com.plantain.intelligentagent.data.model.QwenResponse

interface QwenDataSource {
    suspend fun chat(prompt: String): QwenResponse

    suspend fun chat(request: QwenRequest): QwenResponse
}
