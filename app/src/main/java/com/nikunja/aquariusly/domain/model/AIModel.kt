package com.nikunja.aquariusly.domain.model

import androidx.compose.ui.graphics.Color
import com.nikunja.aquariusly.ui.theme.*

enum class AIProvider {
    OPENAI, ANTHROPIC, GOOGLE, XAI, DEEPSEEK, MISTRAL, META, ALIBABA
}

data class AIModel(
    val id: String,
    val name: String,
    val provider: AIProvider,
    val description: String,
    val brandColor: Color,
    val iconRes: String,
    val isPremium: Boolean = false,
    val maxTokens: Int = 4096,
    val capabilities: List<String> = emptyList()
)

object AIModels {
    val chatGPT = AIModel(
        id = "gpt-4",
        name = "ChatGPT",
        provider = AIProvider.OPENAI,
        description = "OpenAI's most capable model",
        brandColor = ChatGPTGreen,
        iconRes = "chatgpt",
        isPremium = true,
        maxTokens = 8192,
        capabilities = listOf("Code", "Analysis", "Creative", "Math")
    )
    
    val claude = AIModel(
        id = "claude-3",
        name = "Claude",
        provider = AIProvider.ANTHROPIC,
        description = "Anthropic's helpful assistant",
        brandColor = ClaudeOrange,
        iconRes = "claude",
        isPremium = true,
        maxTokens = 100000,
        capabilities = listOf("Long Context", "Analysis", "Code", "Writing")
    )
    
    val gemini = AIModel(
        id = "gemini-pro",
        name = "Gemini",
        provider = AIProvider.GOOGLE,
        description = "Google's multimodal AI",
        brandColor = GeminiBlue,
        iconRes = "gemini",
        isPremium = false,
        maxTokens = 32000,
        capabilities = listOf("Multimodal", "Code", "Research", "Creative")
    )
    
    val grok = AIModel(
        id = "grok-2",
        name = "Grok",
        provider = AIProvider.XAI,
        description = "xAI's witty assistant",
        brandColor = GrokRed,
        iconRes = "grok",
        isPremium = true,
        maxTokens = 8192,
        capabilities = listOf("Real-time", "Humor", "Analysis", "Code")
    )
    
    val mistral = AIModel(
        id = "mistral-large",
        name = "Mistral",
        provider = AIProvider.MISTRAL,
        description = "European AI excellence",
        brandColor = MistralPurple,
        iconRes = "mistral",
        isPremium = false,
        maxTokens = 32000,
        capabilities = listOf("Multilingual", "Code", "Fast", "Efficient")
    )
    
    val llama = AIModel(
        id = "llama-3",
        name = "Llama",
        provider = AIProvider.META,
        description = "Meta's open-source powerhouse",
        brandColor = LlamaBlue,
        iconRes = "llama",
        isPremium = false,
        maxTokens = 8192,
        capabilities = listOf("Open Source", "Code", "Research", "Fast")
    )
    
    val deepseek = AIModel(
        id = "deepseek-v3",
        name = "DeepSeek",
        provider = AIProvider.DEEPSEEK,
        description = "Advanced reasoning AI",
        brandColor = DeepSeekBlue,
        iconRes = "deepseek",
        isPremium = false,
        maxTokens = 64000,
        capabilities = listOf("Reasoning", "Code", "Math", "Analysis")
    )
    
    val qwen = AIModel(
        id = "qwen-max",
        name = "Qwen",
        provider = AIProvider.ALIBABA,
        description = "Alibaba's powerful AI",
        brandColor = QwenPurple,
        iconRes = "qwen",
        isPremium = false,
        maxTokens = 32000,
        capabilities = listOf("Multilingual", "Code", "Creative", "Analysis")
    )
    
    val allModels = listOf(chatGPT, claude, gemini, grok, deepseek, mistral, llama, qwen)
}
