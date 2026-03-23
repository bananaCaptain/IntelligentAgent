package com.plantain.intelligentagent.data.model


//{"choices":[{"finish_reason":"stop","index":0,"message":{"content":"It looks like your message might be incomplete or there could be a typo. Could you please clarify or provide more details about what you're asking? I'm here to help! 😊","role":"assistant"}}],"created":1774263753,"id":"chatcmpl-8d009ad7-b3de-946d-9a64-7b2f71f76bf3","model":"qwen-turbo","object":"chat.completion","usage":{"completion_tokens":37,"prompt_tokens":14,"prompt_tokens_details":{"cached_tokens":0},"total_tokens":51}}
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
