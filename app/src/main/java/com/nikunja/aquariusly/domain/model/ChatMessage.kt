package com.nikunja.aquariusly.domain.model

data class ChatMessage(
    val role: String,
    val content: String
) {
    companion object {
        const val ROLE_USER = "user"
        const val ROLE_ASSISTANT = "assistant"
        const val ROLE_SYSTEM = "system"
    }
}

data class ChatResponse(
    val id: String,
    val model: String,
    val content: String,
    val reasoning: String? = null,
    val usage: ChatUsage,
    val cost: ChatCost,
    val finishReason: String
)

data class ChatUsage(
    val promptTokens: Int,
    val completionTokens: Int,
    val reasoningTokens: Int,
    val cachedTokens: Int,
    val totalTokens: Int
)

data class ChatCost(
    val prompt: Double,
    val completion: Double,
    val reasoning: Double,
    val total: Double,
    val totalInr: Double
)

data class StreamChunk(
    val id: String,
    val content: String,
    val reasoning: String? = null,
    val finishReason: String?,
    val isComplete: Boolean = false,
    val error: String? = null
) {
    val hasContent: Boolean get() = content.isNotEmpty()
    val hasReasoning: Boolean get() = !reasoning.isNullOrEmpty()
}
