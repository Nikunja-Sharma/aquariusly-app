package com.nikunja.aquariusly.domain.model

data class Message(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val modelId: String? = null,
    val isLoading: Boolean = false,
    val isStreaming: Boolean = false,
    val attachments: List<MessageAttachment> = emptyList(),
    val reasoning: String? = null
) {
    val hasReasoning: Boolean get() = !reasoning.isNullOrEmpty()
}

data class Conversation(
    val id: String,
    val title: String,
    val modelId: String,
    val messages: List<Message> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
