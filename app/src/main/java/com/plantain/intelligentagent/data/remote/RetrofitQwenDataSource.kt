package com.plantain.intelligentagent.data.remote

import android.util.Log
import com.plantain.intelligentagent.data.model.QwenRequest
import com.plantain.intelligentagent.data.model.QwenResponse

class RetrofitQwenDataSource(
    private val service: QwenService,
    private val apiKey: String,
    private val model: String = "qwen-turbo"
) : QwenDataSource {

    private companion object {
        const val TAG = "QwenDataSource"
    }

    override suspend fun chat(prompt: String): QwenResponse {
        Log.d(TAG, "chat(prompt=$prompt)")
        return chat(
            QwenRequest(
                model = model,
                messages = listOf(
                    QwenRequest.Message(
                        role = "user",
                        content = prompt
                    )
                )
            )
        )
    }

    override suspend fun chat(request: QwenRequest): QwenResponse {
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
