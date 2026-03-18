package com.plantain.intelligentagent.data.model

data class ZaiRequest(
    val model: String = "glm-4-flash",
    val messages: List<Message>,
    val temperature: Double? = null
) {
    data class Message(
        val role: String,
        val content: String
    )
}
