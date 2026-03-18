package com.plantain.intelligentagent.data.remote

import com.plantain.intelligentagent.data.model.ZaiRequest
import com.plantain.intelligentagent.data.model.ZaiResponse

class RetrofitZaiDataSource(
    private val service: ZaiService,
    private val apiKey: String,
    private val model: String = "glm-4-flash"
) : ZaiDataSource {

    override suspend fun chat(prompt: String): ZaiResponse {
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
        return service.chatCompletions(
            authorization = "Bearer $apiKey",
            request = request
        )
    }
}
