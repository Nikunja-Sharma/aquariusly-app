package com.nikunja.aquariusly.ui.screens.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nikunja.aquariusly.R
import com.nikunja.aquariusly.domain.model.AIModel
import com.nikunja.aquariusly.domain.model.AIProvider
import com.nikunja.aquariusly.domain.model.ConfigurableModel
import com.nikunja.aquariusly.ui.theme.*

data class TokenSettings(
    val maxTokens: Int = 4096,
    val temperature: Float = 0.7f,
    val topP: Float = 1.0f,
    val frequencyPenalty: Float = 0.0f,
    val presencePenalty: Float = 0.0f
)

// Quick presets for generation
enum class GenerationPreset(val label: String, val icon: String, val settings: TokenSettings) {
    PRECISE("Precise", "🎯", TokenSettings(maxTokens = 2048, temperature = 0.3f, topP = 0.9f)),
    BALANCED("Balanced", "⚖️", TokenSettings(maxTokens = 4096, temperature = 0.7f, topP = 1.0f)),
    CREATIVE("Creative", "✨", TokenSettings(maxTokens = 4096, temperature = 1.2f, topP = 0.95f)),
    LONG("Long Output", "📝", TokenSettings(maxTokens = 16384, temperature = 0.7f, topP = 1.0f))
}

// Model categories
enum class ModelCategory { CHAT, IMAGE_GEN, IMAGE_EDIT }

data class ProviderInfo(
    val provider: AIProvider,
    val displayName: String,
    val iconRes: Int,
    val brandColor: Color,
    val models: List<AIModel>
)

// Image generation models
data class ImageModel(
    val id: String,
    val name: String,
    val provider: String,
    val description: String,
    val iconRes: Int,
    val brandColor: Color
)

private val imageGenModels = listOf(
    ImageModel("dall-e-3", "DALL·E 3", "OpenAI", "Best quality", R.drawable.ic_chatgpt, ChatGPTGreen),
    ImageModel("dall-e-2", "DALL·E 2", "OpenAI", "Fast & affordable", R.drawable.ic_chatgpt, ChatGPTGreen),
    ImageModel("stable-diffusion-xl", "SDXL", "Stability", "Open source", R.drawable.ic_mistral, MistralPurple),
    ImageModel("midjourney", "Midjourney", "Midjourney", "Artistic style", R.drawable.ic_gemini, GeminiBlue),
    ImageModel("flux-pro", "Flux Pro", "Black Forest", "Photorealistic", R.drawable.ic_deepseek, DeepSeekBlue),
    ImageModel("ideogram", "Ideogram", "Ideogram", "Text in images", R.drawable.ic_qwen, QwenPurple)
)

private val imageEditModels = listOf(
    ImageModel("dall-e-2-edit", "DALL·E 2 Edit", "OpenAI", "Inpainting & edits", R.drawable.ic_chatgpt, ChatGPTGreen),
    ImageModel("stable-diffusion-inpaint", "SD Inpaint", "Stability", "Mask-based editing", R.drawable.ic_mistral, MistralPurple),
    ImageModel("imagen-edit", "Imagen Edit", "Google", "Smart editing", R.drawable.ic_gemini, GeminiBlue)
)

// Chat model providers
private val chatProviderGroups = listOf(
    ProviderInfo(
        provider = AIProvider.OPENAI,
        displayName = "OpenAI",
        iconRes = R.drawable.ic_chatgpt,
        brandColor = ChatGPTGreen,
        models = listOf(
            AIModel("gpt-4o", "GPT-4o", AIProvider.OPENAI, "Most capable", ChatGPTGreen, R.drawable.ic_chatgpt, true, 128000),
            AIModel("gpt-4o-mini", "GPT-4o Mini", AIProvider.OPENAI, "Fast & affordable", ChatGPTGreen, R.drawable.ic_chatgpt, false, 128000),
            AIModel("o1", "o1", AIProvider.OPENAI, "Advanced reasoning", ChatGPTGreen, R.drawable.ic_chatgpt, true, 200000),
            AIModel("o1-preview", "o1 Preview", AIProvider.OPENAI, "Reasoning preview", ChatGPTGreen, R.drawable.ic_chatgpt, true, 128000),
            AIModel("o1-mini", "o1 Mini", AIProvider.OPENAI, "Fast reasoning", ChatGPTGreen, R.drawable.ic_chatgpt, true, 128000),
            AIModel("o3-mini", "o3 Mini", AIProvider.OPENAI, "Latest reasoning", ChatGPTGreen, R.drawable.ic_chatgpt, true, 200000),
            AIModel("gpt-4-turbo", "GPT-4 Turbo", AIProvider.OPENAI, "Vision capable", ChatGPTGreen, R.drawable.ic_chatgpt, true, 128000),
            AIModel("gpt-4", "GPT-4", AIProvider.OPENAI, "Original GPT-4", ChatGPTGreen, R.drawable.ic_chatgpt, true, 8192),
            AIModel("gpt-3.5-turbo", "GPT-3.5 Turbo", AIProvider.OPENAI, "Legacy fast", ChatGPTGreen, R.drawable.ic_chatgpt, false, 16385),
            AIModel("chatgpt-4o-latest", "ChatGPT-4o Latest", AIProvider.OPENAI, "Dynamic model", ChatGPTGreen, R.drawable.ic_chatgpt, true, 128000)
        )
    ),
    ProviderInfo(
        provider = AIProvider.ANTHROPIC,
        displayName = "Anthropic",
        iconRes = R.drawable.ic_claude,
        brandColor = ClaudeOrange,
        models = listOf(
            AIModel("claude-sonnet-4", "Claude Sonnet 4", AIProvider.ANTHROPIC, "Latest balanced", ClaudeOrange, R.drawable.ic_claude, true, 200000),
            AIModel("claude-opus-4", "Claude Opus 4", AIProvider.ANTHROPIC, "Most powerful", ClaudeOrange, R.drawable.ic_claude, true, 200000),
            AIModel("claude-3-7-sonnet", "Claude 3.7 Sonnet", AIProvider.ANTHROPIC, "Extended thinking", ClaudeOrange, R.drawable.ic_claude, true, 200000),
            AIModel("claude-3-5-sonnet", "Claude 3.5 Sonnet", AIProvider.ANTHROPIC, "Best balance", ClaudeOrange, R.drawable.ic_claude, true, 200000),
            AIModel("claude-3-5-haiku", "Claude 3.5 Haiku", AIProvider.ANTHROPIC, "Fast & smart", ClaudeOrange, R.drawable.ic_claude, false, 200000),
            AIModel("claude-3-opus", "Claude 3 Opus", AIProvider.ANTHROPIC, "Complex tasks", ClaudeOrange, R.drawable.ic_claude, true, 200000),
            AIModel("claude-3-sonnet", "Claude 3 Sonnet", AIProvider.ANTHROPIC, "Balanced", ClaudeOrange, R.drawable.ic_claude, true, 200000),
            AIModel("claude-3-haiku", "Claude 3 Haiku", AIProvider.ANTHROPIC, "Instant", ClaudeOrange, R.drawable.ic_claude, false, 200000),
            AIModel("claude-2.1", "Claude 2.1", AIProvider.ANTHROPIC, "Legacy 200K", ClaudeOrange, R.drawable.ic_claude, false, 200000),
            AIModel("claude-instant", "Claude Instant", AIProvider.ANTHROPIC, "Legacy fast", ClaudeOrange, R.drawable.ic_claude, false, 100000)
        )
    ),
    ProviderInfo(
        provider = AIProvider.GOOGLE,
        displayName = "Google",
        iconRes = R.drawable.ic_gemini,
        brandColor = GeminiBlue,
        models = listOf(
            AIModel("gemini-2.0-flash", "Gemini 2.0 Flash", AIProvider.GOOGLE, "Latest fast", GeminiBlue, R.drawable.ic_gemini, false, 1000000),
            AIModel("gemini-2.0-flash-thinking", "Gemini 2.0 Flash Thinking", AIProvider.GOOGLE, "Reasoning", GeminiBlue, R.drawable.ic_gemini, true, 1000000),
            AIModel("gemini-2.0-pro", "Gemini 2.0 Pro", AIProvider.GOOGLE, "Most capable", GeminiBlue, R.drawable.ic_gemini, true, 2000000),
            AIModel("gemini-1.5-pro", "Gemini 1.5 Pro", AIProvider.GOOGLE, "Advanced", GeminiBlue, R.drawable.ic_gemini, true, 2000000),
            AIModel("gemini-1.5-flash", "Gemini 1.5 Flash", AIProvider.GOOGLE, "Fast", GeminiBlue, R.drawable.ic_gemini, false, 1000000),
            AIModel("gemini-1.5-flash-8b", "Gemini 1.5 Flash 8B", AIProvider.GOOGLE, "Lightweight", GeminiBlue, R.drawable.ic_gemini, false, 1000000),
            AIModel("gemini-1.0-pro", "Gemini 1.0 Pro", AIProvider.GOOGLE, "Legacy", GeminiBlue, R.drawable.ic_gemini, false, 32000),
            AIModel("gemma-2-27b", "Gemma 2 27B", AIProvider.GOOGLE, "Open weights", GeminiBlue, R.drawable.ic_gemini, false, 8192),
            AIModel("gemma-2-9b", "Gemma 2 9B", AIProvider.GOOGLE, "Efficient", GeminiBlue, R.drawable.ic_gemini, false, 8192),
            AIModel("gemma-2-2b", "Gemma 2 2B", AIProvider.GOOGLE, "Ultra light", GeminiBlue, R.drawable.ic_gemini, false, 8192)
        )
    ),
    ProviderInfo(
        provider = AIProvider.XAI,
        displayName = "xAI",
        iconRes = R.drawable.ic_grok,
        brandColor = GrokRed,
        models = listOf(
            AIModel("grok-3", "Grok 3", AIProvider.XAI, "Most powerful", GrokRed, R.drawable.ic_grok, true, 131072),
            AIModel("grok-3-mini", "Grok 3 Mini", AIProvider.XAI, "Fast thinking", GrokRed, R.drawable.ic_grok, false, 131072),
            AIModel("grok-2", "Grok 2", AIProvider.XAI, "Advanced", GrokRed, R.drawable.ic_grok, true, 131072),
            AIModel("grok-2-mini", "Grok 2 Mini", AIProvider.XAI, "Fast", GrokRed, R.drawable.ic_grok, false, 131072),
            AIModel("grok-2-vision", "Grok 2 Vision", AIProvider.XAI, "Multimodal", GrokRed, R.drawable.ic_grok, true, 32768),
            AIModel("grok-1.5", "Grok 1.5", AIProvider.XAI, "128K context", GrokRed, R.drawable.ic_grok, false, 128000),
            AIModel("grok-1.5-vision", "Grok 1.5 Vision", AIProvider.XAI, "Image understanding", GrokRed, R.drawable.ic_grok, true, 128000),
            AIModel("grok-1", "Grok 1", AIProvider.XAI, "Original", GrokRed, R.drawable.ic_grok, false, 8192),
            AIModel("grok-beta", "Grok Beta", AIProvider.XAI, "Experimental", GrokRed, R.drawable.ic_grok, false, 131072),
            AIModel("grok-vision-beta", "Grok Vision Beta", AIProvider.XAI, "Vision preview", GrokRed, R.drawable.ic_grok, false, 8192)
        )
    ),
    ProviderInfo(
        provider = AIProvider.DEEPSEEK,
        displayName = "DeepSeek",
        iconRes = R.drawable.ic_deepseek,
        brandColor = DeepSeekBlue,
        models = listOf(
            AIModel("deepseek-chat", "DeepSeek V3", AIProvider.DEEPSEEK, "Latest chat", DeepSeekBlue, R.drawable.ic_deepseek, false, 64000),
            AIModel("deepseek-reasoner", "DeepSeek R1", AIProvider.DEEPSEEK, "Advanced reasoning", DeepSeekBlue, R.drawable.ic_deepseek, false, 64000),
            AIModel("deepseek-r1-lite", "DeepSeek R1 Lite", AIProvider.DEEPSEEK, "Fast reasoning", DeepSeekBlue, R.drawable.ic_deepseek, false, 64000),
            AIModel("deepseek-v2.5", "DeepSeek V2.5", AIProvider.DEEPSEEK, "Balanced", DeepSeekBlue, R.drawable.ic_deepseek, false, 128000),
            AIModel("deepseek-v2", "DeepSeek V2", AIProvider.DEEPSEEK, "MoE model", DeepSeekBlue, R.drawable.ic_deepseek, false, 128000),
            AIModel("deepseek-coder-v2", "DeepSeek Coder V2", AIProvider.DEEPSEEK, "Code expert", DeepSeekBlue, R.drawable.ic_deepseek, false, 128000),
            AIModel("deepseek-coder", "DeepSeek Coder", AIProvider.DEEPSEEK, "Code generation", DeepSeekBlue, R.drawable.ic_deepseek, false, 16000),
            AIModel("deepseek-math", "DeepSeek Math", AIProvider.DEEPSEEK, "Math specialist", DeepSeekBlue, R.drawable.ic_deepseek, false, 4096),
            AIModel("deepseek-llm-67b", "DeepSeek LLM 67B", AIProvider.DEEPSEEK, "Large model", DeepSeekBlue, R.drawable.ic_deepseek, false, 4096),
            AIModel("deepseek-lite", "DeepSeek Lite", AIProvider.DEEPSEEK, "Lightweight", DeepSeekBlue, R.drawable.ic_deepseek, false, 32000)
        )
    ),
    ProviderInfo(
        provider = AIProvider.MISTRAL,
        displayName = "Mistral",
        iconRes = R.drawable.ic_mistral,
        brandColor = MistralPurple,
        models = listOf(
            AIModel("mistral-large", "Mistral Large", AIProvider.MISTRAL, "Most capable", MistralPurple, R.drawable.ic_mistral, true, 128000),
            AIModel("mistral-medium", "Mistral Medium", AIProvider.MISTRAL, "Balanced", MistralPurple, R.drawable.ic_mistral, true, 32000),
            AIModel("mistral-small", "Mistral Small", AIProvider.MISTRAL, "Fast", MistralPurple, R.drawable.ic_mistral, false, 32000),
            AIModel("mistral-nemo", "Mistral Nemo", AIProvider.MISTRAL, "12B efficient", MistralPurple, R.drawable.ic_mistral, false, 128000),
            AIModel("codestral", "Codestral", AIProvider.MISTRAL, "Code expert", MistralPurple, R.drawable.ic_mistral, true, 32000),
            AIModel("codestral-mamba", "Codestral Mamba", AIProvider.MISTRAL, "Fast code", MistralPurple, R.drawable.ic_mistral, false, 256000),
            AIModel("pixtral-large", "Pixtral Large", AIProvider.MISTRAL, "Vision large", MistralPurple, R.drawable.ic_mistral, true, 128000),
            AIModel("pixtral-12b", "Pixtral 12B", AIProvider.MISTRAL, "Vision model", MistralPurple, R.drawable.ic_mistral, false, 128000),
            AIModel("mixtral-8x22b", "Mixtral 8x22B", AIProvider.MISTRAL, "MoE flagship", MistralPurple, R.drawable.ic_mistral, true, 65000),
            AIModel("mixtral-8x7b", "Mixtral 8x7B", AIProvider.MISTRAL, "MoE efficient", MistralPurple, R.drawable.ic_mistral, false, 32000)
        )
    ),
    ProviderInfo(
        provider = AIProvider.META,
        displayName = "Meta",
        iconRes = R.drawable.ic_llama,
        brandColor = LlamaBlue,
        models = listOf(
            AIModel("llama-3.3-70b", "Llama 3.3 70B", AIProvider.META, "Latest 70B", LlamaBlue, R.drawable.ic_llama, false, 128000),
            AIModel("llama-3.2-90b-vision", "Llama 3.2 90B Vision", AIProvider.META, "Multimodal", LlamaBlue, R.drawable.ic_llama, true, 128000),
            AIModel("llama-3.2-11b-vision", "Llama 3.2 11B Vision", AIProvider.META, "Vision efficient", LlamaBlue, R.drawable.ic_llama, false, 128000),
            AIModel("llama-3.2-3b", "Llama 3.2 3B", AIProvider.META, "Lightweight", LlamaBlue, R.drawable.ic_llama, false, 128000),
            AIModel("llama-3.2-1b", "Llama 3.2 1B", AIProvider.META, "Ultra light", LlamaBlue, R.drawable.ic_llama, false, 128000),
            AIModel("llama-3.1-405b", "Llama 3.1 405B", AIProvider.META, "Largest open", LlamaBlue, R.drawable.ic_llama, true, 128000),
            AIModel("llama-3.1-70b", "Llama 3.1 70B", AIProvider.META, "Balanced", LlamaBlue, R.drawable.ic_llama, false, 128000),
            AIModel("llama-3.1-8b", "Llama 3.1 8B", AIProvider.META, "Efficient", LlamaBlue, R.drawable.ic_llama, false, 128000),
            AIModel("llama-3-70b", "Llama 3 70B", AIProvider.META, "Original 70B", LlamaBlue, R.drawable.ic_llama, false, 8192),
            AIModel("llama-3-8b", "Llama 3 8B", AIProvider.META, "Original 8B", LlamaBlue, R.drawable.ic_llama, false, 8192)
        )
    ),
    ProviderInfo(
        provider = AIProvider.ALIBABA,
        displayName = "Alibaba",
        iconRes = R.drawable.ic_qwen,
        brandColor = QwenPurple,
        models = listOf(
            AIModel("qwen-max", "Qwen Max", AIProvider.ALIBABA, "Most capable", QwenPurple, R.drawable.ic_qwen, true, 32000),
            AIModel("qwen-plus", "Qwen Plus", AIProvider.ALIBABA, "Enhanced", QwenPurple, R.drawable.ic_qwen, true, 131072),
            AIModel("qwen-turbo", "Qwen Turbo", AIProvider.ALIBABA, "Fast", QwenPurple, R.drawable.ic_qwen, false, 131072),
            AIModel("qwen2.5-72b", "Qwen 2.5 72B", AIProvider.ALIBABA, "Latest large", QwenPurple, R.drawable.ic_qwen, true, 128000),
            AIModel("qwen2.5-32b", "Qwen 2.5 32B", AIProvider.ALIBABA, "Balanced", QwenPurple, R.drawable.ic_qwen, false, 128000),
            AIModel("qwen2.5-14b", "Qwen 2.5 14B", AIProvider.ALIBABA, "Efficient", QwenPurple, R.drawable.ic_qwen, false, 128000),
            AIModel("qwen2.5-7b", "Qwen 2.5 7B", AIProvider.ALIBABA, "Lightweight", QwenPurple, R.drawable.ic_qwen, false, 128000),
            AIModel("qwen2.5-coder-32b", "Qwen 2.5 Coder 32B", AIProvider.ALIBABA, "Code expert", QwenPurple, R.drawable.ic_qwen, true, 128000),
            AIModel("qwen-vl-max", "Qwen VL Max", AIProvider.ALIBABA, "Vision large", QwenPurple, R.drawable.ic_qwen, true, 32000),
            AIModel("qwen-vl-plus", "Qwen VL Plus", AIProvider.ALIBABA, "Vision model", QwenPurple, R.drawable.ic_qwen, false, 8192)
        )
    )
)

@Composable
fun ModelSettingsPanel(
    isVisible: Boolean,
    selectedModel: AIModel,
    selectedConfigurableModel: ConfigurableModel?,
    preferredModels: List<ConfigurableModel>,
    tokenSettings: TokenSettings,
    expandedProviders: Set<AIProvider>,
    onProviderToggle: (AIProvider) -> Unit,
    onModelSelect: (AIModel) -> Unit,
    onConfigurableModelSelect: (ConfigurableModel) -> Unit,
    onTokenSettingsChange: (TokenSettings) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = UnifiedTheme.colors
    var selectedCategory by remember { mutableStateOf(ModelCategory.CHAT) }
    var selectedPreset by remember { mutableStateOf(GenerationPreset.BALANCED) }
    var selectedImageGenModel by remember { mutableStateOf<String?>(null) }
    var selectedImageEditModel by remember { mutableStateOf<String?>(null) }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
        exit = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(onClick = onDismiss)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(320.dp)
                    .align(Alignment.CenterEnd)
                    .clickable(enabled = false) {},
                color = colors.backgroundPrimary,
                shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) {
                    PanelHeader(onDismiss = onDismiss)
                    
                    // Category Tabs
                    CategoryTabs(
                        selectedCategory = selectedCategory,
                        onCategorySelect = { selectedCategory = it }
                    )
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (selectedCategory) {
                            ModelCategory.CHAT -> {
                                // Quick Settings
                                item {
                                    QuickSettingsSection(
                                        selectedPreset = selectedPreset,
                                        onPresetSelect = { preset ->
                                            selectedPreset = preset
                                            onTokenSettingsChange(preset.settings)
                                        }
                                    )
                                }
                                
                                item { Spacer(modifier = Modifier.height(8.dp)) }
                                
                                // Chat Models
                                chatProviderGroups.forEach { providerInfo ->
                                    item {
                                        ProviderSection(
                                            providerInfo = providerInfo,
                                            isExpanded = expandedProviders.contains(providerInfo.provider),
                                            selectedModelId = selectedConfigurableModel?.id ?: selectedModel.id,
                                            onToggle = { onProviderToggle(providerInfo.provider) },
                                            onModelSelect = onModelSelect
                                        )
                                    }
                                }
                            }
                            
                            ModelCategory.IMAGE_GEN -> {
                                item {
                                    Text(
                                        text = "Image Generation",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                                
                                imageGenModels.forEach { model ->
                                    item {
                                        ImageModelItem(
                                            model = model,
                                            isSelected = selectedImageGenModel == model.id,
                                            onClick = { selectedImageGenModel = model.id }
                                        )
                                    }
                                }
                            }
                            
                            ModelCategory.IMAGE_EDIT -> {
                                item {
                                    Text(
                                        text = "Image Editing",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                                
                                imageEditModels.forEach { model ->
                                    item {
                                        ImageModelItem(
                                            model = model,
                                            isSelected = selectedImageEditModel == model.id,
                                            onClick = { selectedImageEditModel = model.id }
                                        )
                                    }
                                }
                            }
                        }
                        
                        item { Spacer(modifier = Modifier.height(32.dp)) }
                    }
                }
            }
        }
    }
}


@Composable
private fun PanelHeader(onDismiss: () -> Unit) {
    val colors = UnifiedTheme.colors
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Models",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
        
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(colors.surfaceElevated)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = colors.textSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun CategoryTabs(
    selectedCategory: ModelCategory,
    onCategorySelect: (ModelCategory) -> Unit
) {
    val colors = UnifiedTheme.colors
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceElevated)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CategoryTab(
            label = "💬 Chat",
            isSelected = selectedCategory == ModelCategory.CHAT,
            onClick = { onCategorySelect(ModelCategory.CHAT) },
            modifier = Modifier.weight(1f)
        )
        CategoryTab(
            label = "🎨 Generate",
            isSelected = selectedCategory == ModelCategory.IMAGE_GEN,
            onClick = { onCategorySelect(ModelCategory.IMAGE_GEN) },
            modifier = Modifier.weight(1f)
        )
        CategoryTab(
            label = "✏️ Edit",
            isSelected = selectedCategory == ModelCategory.IMAGE_EDIT,
            onClick = { onCategorySelect(ModelCategory.IMAGE_EDIT) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CategoryTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = UnifiedTheme.colors
    
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) colors.accentMint else Color.Transparent
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color.White else colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        )
    }
}

@Composable
private fun QuickSettingsSection(
    selectedPreset: GenerationPreset,
    onPresetSelect: (GenerationPreset) -> Unit
) {
    val colors = UnifiedTheme.colors
    
    Column {
        Text(
            text = "Quick Settings",
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GenerationPreset.entries.forEach { preset ->
                PresetChip(
                    preset = preset,
                    isSelected = selectedPreset == preset,
                    onClick = { onPresetSelect(preset) }
                )
            }
        }
    }
}

@Composable
private fun PresetChip(
    preset: GenerationPreset,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) colors.accentMint.copy(alpha = 0.15f) else colors.surfaceElevated,
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(1.5.dp, colors.accentMint)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, colors.borderDefault)
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = preset.icon, fontSize = 14.sp)
            Text(
                text = preset.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) colors.accentMint else colors.textPrimary
            )
        }
    }
}

@Composable
private fun ProviderSection(
    providerInfo: ProviderInfo,
    isExpanded: Boolean,
    selectedModelId: String,
    onToggle: () -> Unit,
    onModelSelect: (AIModel) -> Unit
) {
    val colors = UnifiedTheme.colors
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(200),
        label = "rotation"
    )
    
    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle),
            shape = RoundedCornerShape(12.dp),
            color = colors.surfaceElevated
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(providerInfo.brandColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = providerInfo.iconRes),
                        contentDescription = providerInfo.displayName,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                }
                
                Spacer(modifier = Modifier.width(10.dp))
                
                Text(
                    text = providerInfo.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = "${providerInfo.models.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textTertiary
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(rotationAngle),
                    tint = colors.textSecondary
                )
            }
        }
        
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = tween(200)) + fadeIn(),
            exit = shrinkVertically(animationSpec = tween(200)) + fadeOut()
        ) {
            Column(
                modifier = Modifier.padding(start = 12.dp, top = 6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                providerInfo.models.forEach { model ->
                    ModelSubItem(
                        model = model,
                        isSelected = selectedModelId == model.id,
                        onClick = { onModelSelect(model) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModelSubItem(
    model: AIModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) model.brandColor.copy(alpha = 0.12f) else Color.Transparent,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, model.brandColor) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = model.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) model.brandColor else colors.textPrimary
                    )
                    if (model.isPremium) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = colors.accentAmber.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "PRO",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.accentAmber,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                fontSize = 9.sp
                            )
                        }
                    }
                }
                Text(
                    text = model.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textTertiary,
                    fontSize = 11.sp
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = model.brandColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ImageModelItem(
    model: ImageModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) model.brandColor.copy(alpha = 0.12f) else colors.surfaceElevated,
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(1.5.dp, model.brandColor)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, colors.borderDefault)
        }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(model.brandColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = model.iconRes),
                    contentDescription = model.name,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = model.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) model.brandColor else colors.textPrimary
                )
                Text(
                    text = "${model.provider} • ${model.description}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textTertiary
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = model.brandColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
