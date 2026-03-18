package com.plantain.intelligentagent.data.remote

import com.plantain.intelligentagent.data.model.QwenRequest
import com.plantain.intelligentagent.data.model.QwenResponse

class RetrofitQwenDataSource(
    private val service: QwenService,
    private val apiKey: String,
    private val model: String = "qwen-turbo"
) : QwenDataSource {

    override suspend fun chat(prompt: String): QwenResponse {
        return chat(
            QwenRequest(
                model = model,
                input = QwenRequest.Input(
                    messages = listOf(
                        QwenRequest.Message(
                            role = "user",
                            content = prompt
                        )
                    )
                )
            )
        )
    }

    override suspend fun chat(request: QwenRequest): QwenResponse {
        return service.chatCompletions(
            authorization = "Bearer $apiKey",
            request = request
        )
    }
}
