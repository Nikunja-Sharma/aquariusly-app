package com.nikunja.aquariusly.data.repository

import android.util.Log
import com.google.gson.Gson
import com.nikunja.aquariusly.data.remote.api.ChatApiService
import com.nikunja.aquariusly.data.remote.dto.ChatMessageDto
import com.nikunja.aquariusly.data.remote.dto.ChatRequestDto
import com.nikunja.aquariusly.data.remote.dto.StreamChunkDto
import com.nikunja.aquariusly.domain.model.ChatCost
import com.nikunja.aquariusly.domain.model.ChatMessage
import com.nikunja.aquariusly.domain.model.ChatResponse
import com.nikunja.aquariusly.domain.model.ChatUsage
import com.nikunja.aquariusly.domain.model.StreamChunk
import com.nikunja.aquariusly.domain.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class ChatRepositoryImpl @Inject constructor(
    private val chatApiService: ChatApiService,
    private val gson: Gson
) : ChatRepository {

    companion object {
        private const val TAG = "ChatRepository"
    }

    override suspend fun sendMessage(
        model: String,
        messages: List<ChatMessage>,
        temperature: Double?,
        maxTokens: Int?
    ): Result<ChatResponse> {
        return try {
            val request = ChatRequestDto(
                model = model,
                messages = messages.map { ChatMessageDto(it.role, it.content) },
                temperature = temperature,
                maxTokens = maxTokens
            )
            val response = chatApiService.chat(request)
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                Result.success(
                    ChatResponse(
                        id = dto.id,
                        model = dto.model,
                        content = dto.content,
                        reasoning = dto.reasoning,
                        usage = ChatUsage(
                            promptTokens = dto.usage.promptTokens,
                            completionTokens = dto.usage.completionTokens,
                            reasoningTokens = dto.usage.reasoningTokens,
                            cachedTokens = dto.usage.cachedTokens,
                            totalTokens = dto.usage.totalTokens
                        ),
                        cost = ChatCost(
                            prompt = dto.cost.prompt,
                            completion = dto.cost.completion,
                            reasoning = dto.cost.reasoning,
                            total = dto.cost.total,
                            totalInr = dto.cost.totalInr
                        ),
                        finishReason = dto.finishReason
                    )
                )
            } else {
                Result.failure(Exception("Failed to send message: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun sendMessageStream(
        model: String,
        messages: List<ChatMessage>,
        temperature: Double?,
        maxTokens: Int?
    ): Flow<StreamChunk> = flow {
        var reader: BufferedReader? = null
        try {
            val request = ChatRequestDto(
                model = model,
                messages = messages.map { ChatMessageDto(it.role, it.content) },
                temperature = temperature,
                maxTokens = maxTokens
            )
            
            Log.d(TAG, "Starting stream request for model: $model")
            val response = chatApiService.chatStream(request)
            
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.e(TAG, "Stream request failed: ${response.code()} - $errorBody")
                emit(StreamChunk(
                    id = "",
                    content = "",
                    finishReason = null,
                    error = "Request failed: ${response.code()} - $errorBody"
                ))
                return@flow
            }

            val body = response.body()
            if (body == null) {
                Log.e(TAG, "Empty response body")
                emit(StreamChunk(
                    id = "",
                    content = "",
                    finishReason = null,
                    error = "Empty response body"
                ))
                return@flow
            }

            reader = BufferedReader(InputStreamReader(body.byteStream()))
            
            var line: String?
            while (coroutineContext.isActive) {
                line = reader.readLine()
                if (line == null) break
                
                Log.d(TAG, "SSE line: $line")
                
                // Skip empty lines
                if (line.isBlank()) continue
                
                // Handle SSE data lines
                if (line.startsWith("data: ")) {
                    val data = line.removePrefix("data: ").trim()
                    
                    if (data == "[DONE]") {
                        Log.d(TAG, "Stream complete")
                        emit(StreamChunk(
                            id = "",
                            content = "",
                            finishReason = "stop",
                            isComplete = true
                        ))
                        break
                    }
                    
                    try {
                        val chunk = gson.fromJson(data, StreamChunkDto::class.java)
                        Log.d(TAG, "Parsed chunk - content: '${chunk.content}', reasoning: '${chunk.reasoning}'")
                        
                        emit(StreamChunk(
                            id = chunk.id,
                            content = chunk.content,
                            reasoning = chunk.reasoning,
                            finishReason = chunk.finishReason
                        ))
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse chunk: $data", e)
                        // Continue processing other chunks
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Stream error", e)
            emit(StreamChunk(
                id = "",
                content = "",
                finishReason = null,
                error = e.message ?: "Unknown error"
            ))
        } finally {
            try {
                reader?.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing reader", e)
            }
        }
    }.flowOn(Dispatchers.IO)
}
