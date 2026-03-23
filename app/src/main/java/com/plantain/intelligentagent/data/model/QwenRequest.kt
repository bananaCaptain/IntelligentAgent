package com.plantain.intelligentagent.data.model

data class QwenRequest(
    val model: String = "qwen-turbo",
    val messages: List<Message>,
    val parameters: Parameters? = null
) {
//    data class Input(
//        val messages: List<Message>
//    )

    data class Message(
        val role: String,
        val content: String
    )

    data class Parameters(
        val temperature: Double? = null,
        val maxTokens: Int? = null
    )
}
