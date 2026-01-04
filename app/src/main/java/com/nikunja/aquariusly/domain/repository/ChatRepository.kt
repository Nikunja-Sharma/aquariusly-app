package com.nikunja.aquariusly.domain.repository

import com.nikunja.aquariusly.domain.model.ChatMessage
import com.nikunja.aquariusly.domain.model.ChatResponse
import com.nikunja.aquariusly.domain.model.StreamChunk
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(
        model: String,
        messages: List<ChatMessage>,
        temperature: Double? = null,
        maxTokens: Int? = null
    ): Result<ChatResponse>

    fun sendMessageStream(
        model: String,
        messages: List<ChatMessage>,
        temperature: Double? = null,
        maxTokens: Int? = null
    ): Flow<StreamChunk>
}
