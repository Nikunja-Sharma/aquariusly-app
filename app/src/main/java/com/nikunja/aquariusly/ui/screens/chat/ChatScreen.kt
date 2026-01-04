package com.nikunja.aquariusly.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikunja.aquariusly.ui.components.ChatSidebar
import com.nikunja.aquariusly.ui.screens.chat.components.*
import com.nikunja.aquariusly.ui.theme.UnifiedTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    initialChatId: String? = null,
    initialModelId: String? = null,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = UnifiedTheme.colors
    val listState = rememberLazyListState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Handle deep link - open specific chat
    LaunchedEffect(initialChatId) {
        initialChatId?.let { chatId ->
            viewModel.onEvent(ChatEvent.SelectHistory(chatId))
        }
    }
    
    // Handle model selection from navigation
    LaunchedEffect(initialModelId) {
        if (!initialModelId.isNullOrBlank()) {
            viewModel.onEvent(ChatEvent.SetInitialModel(initialModelId))
        }
    }
    
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }
    
    LaunchedEffect(state.isDrawerOpen) {
        if (state.isDrawerOpen) drawerState.open() else drawerState.close()
    }
    
    LaunchedEffect(drawerState.currentValue) {
        if (drawerState.currentValue == DrawerValue.Closed && state.isDrawerOpen) {
            viewModel.onEvent(ChatEvent.ToggleDrawer)
        }
    }
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ChatSidebar(
                selectedModel = state.selectedModel,
                chatHistories = state.chatHistories,
                selectedHistoryId = state.selectedHistoryId,
                onModelSelect = { viewModel.onEvent(ChatEvent.SelectModel(it)) },
                onSelectHistory = { viewModel.onEvent(ChatEvent.SelectHistory(it)) },
                onNewChat = { viewModel.onEvent(ChatEvent.NewChat) },
                onSettingsClick = onSettingsClick,
                onProfileClick = onProfileClick,
                onClose = { 
                    scope.launch { drawerState.close() }
                    viewModel.onEvent(ChatEvent.ToggleDrawer)
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.backgroundPrimary)
                .statusBarsPadding()
                .imePadding()
        ) {
            ChatTopBar(
                selectedModel = state.selectedModel,
                onMenuClick = { 
                    scope.launch { drawerState.open() }
                    viewModel.onEvent(ChatEvent.ToggleDrawer)
                },
                onProfileClick = onProfileClick
            )
            
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (state.messages.isEmpty()) {
                    item { EmptyStateContent(selectedModel = state.selectedModel) }
                }
                
                items(items = state.messages, key = { it.id }) { message ->
                    MessageBubble(message = message, model = state.selectedModel)
                }
                
                if (state.isLoading) {
                    item { 
                        TypingIndicator(
                            model = state.selectedModel,
                            isThinking = state.isThinking,
                            thinkingContent = state.streamingReasoning
                        )
                    }
                }
            }
            
            ChatInputSection(
                inputText = state.inputText,
                onInputChange = { viewModel.onEvent(ChatEvent.UpdateInput(it)) },
                onSend = { viewModel.onEvent(ChatEvent.SendMessage(state.inputText)) },
                isLoading = state.isLoading,
                isActionsExpanded = state.isActionsExpanded,
                onToggleActions = { viewModel.onEvent(ChatEvent.ToggleActionsPanel) },
                activeActions = state.activeActions,
                onToggleAction = { viewModel.onEvent(ChatEvent.ToggleAction(it)) },
                attachments = state.attachments,
                onAddAttachment = { viewModel.onEvent(ChatEvent.AddAttachment(it)) },
                onRemoveAttachment = { viewModel.onEvent(ChatEvent.RemoveAttachment(it)) },
                accentColor = state.selectedModel.brandColor
            )
        }
    }
}
