package com.plantain.intelligentagent.data.remote

import com.plantain.intelligentagent.data.model.ZaiRequest
import com.plantain.intelligentagent.data.model.ZaiResponse

interface ZaiDataSource {
    suspend fun chat(prompt: String): ZaiResponse

    suspend fun chat(request: ZaiRequest): ZaiResponse
}
