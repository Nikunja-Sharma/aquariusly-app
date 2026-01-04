package com.nikunja.aquariusly.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatMessageDto(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class ReasoningConfigDto(
    @SerializedName("enabled") val enabled: Boolean,
    @SerializedName("effort") val effort: String? = null
)

data class ChatRequestDto(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<ChatMessageDto>,
    @SerializedName("temperature") val temperature: Double? = null,
    @SerializedName("maxTokens") val maxTokens: Int? = null,
    @SerializedName("reasoning") val reasoning: ReasoningConfigDto? = null
)

data class UsageDto(
    @SerializedName("promptTokens") val promptTokens: Int,
    @SerializedName("completionTokens") val completionTokens: Int,
    @SerializedName("reasoningTokens") val reasoningTokens: Int,
    @SerializedName("cachedTokens") val cachedTokens: Int,
    @SerializedName("totalTokens") val totalTokens: Int
)

data class CostDto(
    @SerializedName("prompt") val prompt: Double,
    @SerializedName("completion") val completion: Double,
    @SerializedName("reasoning") val reasoning: Double,
    @SerializedName("total") val total: Double,
    @SerializedName("totalInr") val totalInr: Double
)

data class ChatResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("model") val model: String,
    @SerializedName("content") val content: String,
    @SerializedName("reasoning") val reasoning: String? = null,
    @SerializedName("usage") val usage: UsageDto,
    @SerializedName("cost") val cost: CostDto,
    @SerializedName("finishReason") val finishReason: String
)

data class StreamChunkDto(
    @SerializedName("id") val id: String,
    @SerializedName("content") val content: String,
    @SerializedName("reasoning") val reasoning: String? = null,
    @SerializedName("finishReason") val finishReason: String?,
    @SerializedName("usage") val usage: StreamUsageDto? = null
)

data class StreamUsageDto(
    @SerializedName("promptTokens") val promptTokens: Int,
    @SerializedName("completionTokens") val completionTokens: Int,
    @SerializedName("reasoningTokens") val reasoningTokens: Int,
    @SerializedName("cachedTokens") val cachedTokens: Int,
    @SerializedName("totalTokens") val totalTokens: Int,
    @SerializedName("cost") val cost: Double
)
