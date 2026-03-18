package com.plantain.intelligentagent.data.remote

import com.plantain.intelligentagent.data.model.QwenRequest
import com.plantain.intelligentagent.data.model.QwenResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface QwenService {

    @POST("/compatible-mode/v1/chat/completions")
    suspend fun chatCompletions(
        @Header("Authorization") authorization: String,
        @Body request: QwenRequest
    ): QwenResponse
}
