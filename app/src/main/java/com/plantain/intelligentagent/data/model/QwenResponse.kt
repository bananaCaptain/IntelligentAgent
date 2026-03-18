package com.plantain.intelligentagent.data.model

data class QwenResponse(
    val output: Output? = null,
    val requestId: String? = null,
    val usage: Usage? = null
) {
    data class Output(
        val text: String? = null
    )

    data class Usage(
        val inputTokens: Int? = null,
        val outputTokens: Int? = null,
        val totalTokens: Int? = null
    )
}
