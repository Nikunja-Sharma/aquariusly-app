package com.nikunja.aquariusly.domain.usecase

import com.nikunja.aquariusly.domain.model.ChatMessage
import com.nikunja.aquariusly.domain.model.StreamChunk
import com.nikunja.aquariusly.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendChatMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    fun invoke(
        model: String,
        messages: List<ChatMessage>,
        temperature: Double? = null,
        maxTokens: Int? = null
    ): Flow<StreamChunk> {
        return chatRepository.sendMessageStream(
            model = model,
            messages = messages,
            temperature = temperature,
            maxTokens = maxTokens
        )
    }
}
