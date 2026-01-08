package com.nikunja.aquariusly.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikunja.aquariusly.domain.model.AIModel
import com.nikunja.aquariusly.domain.model.AIModels
import com.nikunja.aquariusly.domain.model.AIProvider
import com.nikunja.aquariusly.domain.model.ChatActionType
import com.nikunja.aquariusly.domain.model.ChatMessage
import com.nikunja.aquariusly.domain.model.ConfigurableModel
import com.nikunja.aquariusly.domain.model.Message
import com.nikunja.aquariusly.domain.model.MessageAttachment
import com.nikunja.aquariusly.domain.usecase.GetPreferredModelsUseCase
import com.nikunja.aquariusly.domain.usecase.GetModelsUseCase
import com.nikunja.aquariusly.domain.usecase.SendChatMessageUseCase
import com.nikunja.aquariusly.domain.model.ModelFilters
import com.nikunja.aquariusly.ui.screens.chat.components.TokenSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val getPreferredModelsUseCase: GetPreferredModelsUseCase,
    private val getModelsUseCase: GetModelsUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(ChatState(
        chatHistories = generateMockHistories()
    ))
    val state: StateFlow<ChatState> = _state.asStateFlow()
    
    private var streamJob: Job? = null
    private var preferredModels: List<ConfigurableModel> = emptyList()
    
    init {
        loadPreferredModels()
    }
    
    private fun loadPreferredModels() {
        viewModelScope.launch {
            getPreferredModelsUseCase().onSuccess { preferredIds ->
                if (preferredIds.isNotEmpty()) {
                    // Load model details for preferred models
                    getModelsUseCase(ModelFilters(), page = 1, limit = 500).onSuccess { result ->
                        preferredModels = result.models.filter { it.id in preferredIds }
                        _state.update { it.copy(preferredModels = preferredModels) }
                        
                        // Set first preferred model as selected if available
                        if (preferredModels.isNotEmpty()) {
                            _state.update { it.copy(selectedConfigurableModel = preferredModels.first()) }
                        }
                    }
                }
            }
        }
    }
    
    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.SendMessage -> sendMessage(event.content)
            is ChatEvent.SelectModel -> selectModel(event.model)
            is ChatEvent.SelectConfigurableModel -> selectConfigurableModel(event.model)
            is ChatEvent.SetInitialModel -> setInitialModel(event.modelId)
            is ChatEvent.UpdateInput -> updateInput(event.text)
            is ChatEvent.ToggleDrawer -> toggleDrawer()
            is ChatEvent.ToggleModelSettingsPanel -> toggleModelSettingsPanel()
            is ChatEvent.UpdateTokenSettings -> updateTokenSettings(event.settings)
            is ChatEvent.ToggleModelPanelProvider -> toggleModelPanelProvider(event.provider)
            is ChatEvent.ToggleActionsPanel -> toggleActionsPanel()
            is ChatEvent.ToggleAction -> toggleAction(event.actionType)
            is ChatEvent.ToggleProviderExpanded -> toggleProviderExpanded(event.provider)
            is ChatEvent.SelectHistory -> selectHistory(event.historyId)
            is ChatEvent.NewChat -> newChat()
            is ChatEvent.ClearError -> clearError()
            is ChatEvent.AddAttachment -> addAttachment(event.attachment)
            is ChatEvent.RemoveAttachment -> removeAttachment(event.attachment)
            is ChatEvent.StopGeneration -> stopGeneration()
        }
    }
    
    private fun setInitialModel(modelId: String) {
        viewModelScope.launch {
            // Try to find in preferred models first
            val preferred = preferredModels.find { it.id == modelId }
            if (preferred != null) {
                _state.update { it.copy(selectedConfigurableModel = preferred) }
                return@launch
            }
            
            // Otherwise fetch from API
            getModelsUseCase(ModelFilters(search = modelId), page = 1, limit = 100).onSuccess { result ->
                val model = result.models.find { it.id == modelId }
                if (model != null) {
                    _state.update { it.copy(selectedConfigurableModel = model) }
                }
            }
        }
    }
    
    private fun sendMessage(content: String) {
        val currentAttachments = _state.value.attachments
        if (content.isBlank() && currentAttachments.isEmpty()) return
        
        val currentState = _state.value
        val modelId = currentState.selectedConfigurableModel?.id 
            ?: currentState.selectedModel.id
        
        val userMessage = Message(
            id = UUID.randomUUID().toString(),
            content = content.trim(),
            isFromUser = true,
            modelId = modelId,
            attachments = currentAttachments
        )
        
        _state.update { 
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                isLoading = true,
                activeActions = emptySet(),
                attachments = emptyList(),
                isActionsExpanded = false,
                streamingContent = ""
            )
        }
        
        // Build message history for API
        val chatMessages = buildChatMessages(content)
        
        // Create placeholder for AI response
        val aiMessageId = UUID.randomUUID().toString()
        val aiMessage = Message(
            id = aiMessageId,
            content = "",
            isFromUser = false,
            modelId = modelId,
            isStreaming = true
        )
        
        _state.update { it.copy(messages = it.messages + aiMessage) }
        
        streamJob = viewModelScope.launch {
            var fullContent = ""
            var fullReasoning = ""
            
            sendChatMessageUseCase.invoke(
                model = modelId,
                messages = chatMessages
            ).collect { chunk ->
                if (chunk.error != null) {
                    _state.update { state ->
                        val updatedMessages = state.messages.map { msg ->
                            if (msg.id == aiMessageId) {
                                msg.copy(
                                    content = "Error: ${chunk.error}",
                                    isStreaming = false
                                )
                            } else msg
                        }
                        state.copy(
                            messages = updatedMessages,
                            isLoading = false,
                            isThinking = false,
                            error = chunk.error
                        )
                    }
                    return@collect
                }
                
                // Handle reasoning/thinking content
                if (chunk.hasReasoning) {
                    fullReasoning += chunk.reasoning
                    _state.update { state ->
                        val updatedMessages = state.messages.map { msg ->
                            if (msg.id == aiMessageId) {
                                msg.copy(reasoning = fullReasoning)
                            } else msg
                        }
                        state.copy(
                            messages = updatedMessages,
                            streamingReasoning = fullReasoning,
                            isThinking = true
                        )
                    }
                }
                
                // Handle regular content
                if (chunk.hasContent) {
                    fullContent += chunk.content
                    _state.update { state ->
                        val updatedMessages = state.messages.map { msg ->
                            if (msg.id == aiMessageId) {
                                msg.copy(
                                    content = fullContent,
                                    reasoning = fullReasoning.ifEmpty { null },
                                    isStreaming = true
                                )
                            } else msg
                        }
                        state.copy(
                            messages = updatedMessages,
                            streamingContent = fullContent,
                            isThinking = false
                        )
                    }
                }
                
                if (chunk.isComplete || chunk.finishReason == "stop") {
                    _state.update { state ->
                        val updatedMessages = state.messages.map { msg ->
                            if (msg.id == aiMessageId) {
                                msg.copy(isStreaming = false)
                            } else msg
                        }
                        state.copy(
                            messages = updatedMessages,
                            isLoading = false,
                            streamingContent = "",
                            streamingReasoning = "",
                            isThinking = false
                        )
                    }
                }
            }
        }
    }
    
    private fun buildChatMessages(currentContent: String): List<ChatMessage> {
        val messages = mutableListOf<ChatMessage>()
        
        // Add system prompt first
        messages.add(
            ChatMessage(
                role = ChatMessage.ROLE_SYSTEM,
                content = "You are Aquariusly, a helpful AI assistant."
            )
        )
        
        // Add conversation history (last 10 messages for context)
        _state.value.messages.takeLast(10).dropLast(1).forEach { msg ->
            messages.add(
                ChatMessage(
                    role = if (msg.isFromUser) ChatMessage.ROLE_USER else ChatMessage.ROLE_ASSISTANT,
                    content = msg.content
                )
            )
        }
        
        // Add current user message
        messages.add(ChatMessage(role = ChatMessage.ROLE_USER, content = currentContent))
        
        return messages
    }
    
    private fun stopGeneration() {
        streamJob?.cancel()
        _state.update { it.copy(isLoading = false, streamingContent = "") }
    }
    
    private fun addAttachment(attachment: MessageAttachment) {
        _state.update { it.copy(attachments = it.attachments + attachment) }
    }
    
    private fun removeAttachment(attachment: MessageAttachment) {
        _state.update { it.copy(attachments = it.attachments.filter { a -> a.id != attachment.id }) }
    }
    
    private fun selectModel(model: AIModel) {
        _state.update { 
            it.copy(
                selectedModel = model,
                selectedConfigurableModel = null,
                isDrawerOpen = false,
                isModelSettingsPanelOpen = false
            )
        }
    }
    
    private fun selectConfigurableModel(model: ConfigurableModel) {
        _state.update {
            it.copy(
                selectedConfigurableModel = model,
                isDrawerOpen = false,
                isModelSettingsPanelOpen = false
            )
        }
    }
    
    private fun updateInput(text: String) {
        _state.update { it.copy(inputText = text) }
    }
    
    private fun toggleDrawer() {
        _state.update { it.copy(isDrawerOpen = !it.isDrawerOpen) }
    }
    
    private fun toggleModelSettingsPanel() {
        _state.update { it.copy(isModelSettingsPanelOpen = !it.isModelSettingsPanelOpen) }
    }
    
    private fun updateTokenSettings(settings: TokenSettings) {
        _state.update { it.copy(tokenSettings = settings) }
    }
    
    private fun toggleModelPanelProvider(provider: AIProvider) {
        _state.update { currentState ->
            val newExpanded = if (currentState.modelPanelExpandedProviders.contains(provider)) {
                currentState.modelPanelExpandedProviders - provider
            } else {
                currentState.modelPanelExpandedProviders + provider
            }
            currentState.copy(modelPanelExpandedProviders = newExpanded)
        }
    }

    private fun toggleActionsPanel() {
        _state.update { it.copy(isActionsExpanded = !it.isActionsExpanded) }
    }
    
    private fun toggleAction(actionType: ChatActionType) {
        _state.update { currentState ->
            val newActions = if (currentState.activeActions.contains(actionType)) {
                currentState.activeActions - actionType
            } else {
                currentState.activeActions + actionType
            }
            currentState.copy(activeActions = newActions)
        }
    }
    
    private fun toggleProviderExpanded(provider: AIProvider) {
        _state.update { currentState ->
            val newExpanded = if (currentState.expandedProviders.contains(provider)) {
                currentState.expandedProviders - provider
            } else {
                currentState.expandedProviders + provider
            }
            currentState.copy(expandedProviders = newExpanded)
        }
    }
    
    private fun selectHistory(historyId: String) {
        val history = _state.value.chatHistories.find { it.id == historyId }
        history?.let {
            val model = AIModels.allModels.find { m -> m.id == it.modelId } ?: AIModels.chatGPT
            _state.update { state ->
                state.copy(
                    selectedHistoryId = historyId,
                    selectedModel = model,
                    messages = emptyList(),
                    isDrawerOpen = false
                )
            }
        }
    }
    
    private fun newChat() {
        _state.update {
            it.copy(
                messages = emptyList(),
                selectedHistoryId = null,
                isDrawerOpen = false,
                streamingContent = ""
            )
        }
    }
    
    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    private fun generateMockHistories(): List<ChatHistory> {
        return listOf(
            ChatHistory("1", "Code review help", "gpt-4", AIProvider.OPENAI, "Can you review this code?", System.currentTimeMillis() - 3600000),
            ChatHistory("2", "API design question", "gpt-4", AIProvider.OPENAI, "Best practices for REST APIs", System.currentTimeMillis() - 7200000),
            ChatHistory("3", "Writing assistance", "claude-3", AIProvider.ANTHROPIC, "Help me write documentation", System.currentTimeMillis() - 86400000)
        )
    }
}
