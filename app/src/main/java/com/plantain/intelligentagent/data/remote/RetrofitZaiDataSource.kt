package com.plantain.intelligentagent.data.remote

import android.util.Log
import com.plantain.intelligentagent.data.model.ZaiRequest
import com.plantain.intelligentagent.data.model.ZaiResponse

class RetrofitZaiDataSource(
    private val service: ZaiService,
    private val apiKey: String,
    private val model: String = "glm-4-flash"
) : ZaiDataSource {

    private companion object {
        const val TAG = "ZaiDataSource"
    }

    override suspend fun chat(prompt: String): ZaiResponse {
        Log.d(TAG, "chat(prompt=$prompt)")
        return chat(
            ZaiRequest(
                model = model,
                messages = listOf(
                    ZaiRequest.Message(
                        role = "user",
                        content = prompt
                    )
                )
            )
        )
    }

    override suspend fun chat(request: ZaiRequest): ZaiResponse {
        Log.d(TAG, "request=$request")
        return runCatching {
            service.chatCompletions(
                authorization = "Bearer $apiKey",
                request = request
            )
        }.onSuccess {
            Log.d(TAG, "response=$it")
        }.onFailure {
            Log.e(TAG, "request failed: ${it::class.java.simpleName}: ${it.message}", it)
        }.getOrThrow()
    }
}
