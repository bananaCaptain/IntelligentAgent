package com.plantain.intelligentagent.data.remote

import com.plantain.intelligentagent.data.model.ZaiRequest
import com.plantain.intelligentagent.data.model.ZaiResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ZaiService {

    @POST("/api/paas/v4/chat/completions")
    suspend fun chatCompletions(
        @Header("Authorization") authorization: String,
        @Body request: ZaiRequest
    ): ZaiResponse
}
