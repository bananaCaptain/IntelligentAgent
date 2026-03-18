package com.plantain.intelligentagent.data.model

data class ZaiResponse(
    val choices: List<Choice>? = null,
    val usage: Usage? = null,
    val id: String? = null
) {
    data class Choice(
        val message: Message? = null
    ) {
        data class Message(
            val role: String? = null,
            val content: String? = null
        )
    }

    data class Usage(
        val promptTokens: Int? = null,
        val completionTokens: Int? = null,
        val totalTokens: Int? = null
    )
}
