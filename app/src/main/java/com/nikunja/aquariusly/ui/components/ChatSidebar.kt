package com.nikunja.aquariusly.ui.components

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nikunja.aquariusly.domain.model.AIModel
import com.nikunja.aquariusly.domain.model.AIModels
import com.nikunja.aquariusly.domain.model.AIProvider
import com.nikunja.aquariusly.ui.screens.chat.ChatHistory
import com.nikunja.aquariusly.ui.theme.UnifiedTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSidebar(
    selectedModel: AIModel,
    chatHistories: List<ChatHistory>,
    selectedHistoryId: String?,
    onModelSelect: (AIModel) -> Unit,
    onSelectHistory: (String) -> Unit,
    onNewChat: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onClose: () -> Unit
) {
    val colors = UnifiedTheme.colors
    
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = colors.backgroundPrimary
    ) {
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            // Header
            SidebarHeader(onNewChat = onNewChat, onClose = onClose)
            
            HorizontalDivider(color = colors.borderDefault, thickness = 0.5.dp)
            
            // History Section (main content - scrollable)
            HistorySection(
                histories = chatHistories,
                selectedHistoryId = selectedHistoryId,
                onSelectHistory = onSelectHistory,
                modifier = Modifier.weight(1f)
            )
            
            HorizontalDivider(color = colors.borderDefault, thickness = 0.5.dp)
            
            // Model Selection Section
            ModelSelectionSection(
                selectedModel = selectedModel,
                onModelSelect = onModelSelect
            )
            
            HorizontalDivider(color = colors.borderDefault, thickness = 0.5.dp)
            
            // Bottom Settings Section
            BottomSettingsSection(
                onSettingsClick = onSettingsClick,
                onProfileClick = onProfileClick
            )
        }
    }
}

@Composable
private fun SidebarHeader(
    onNewChat: () -> Unit,
    onClose: () -> Unit
) {
    val colors = UnifiedTheme.colors
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Aquariusly",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // New Chat Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onNewChat),
            shape = RoundedCornerShape(12.dp),
            color = colors.accentBlue
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "New Chat",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun HistorySection(
    histories: List<ChatHistory>,
    selectedHistoryId: String?,
    onSelectHistory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = UnifiedTheme.colors
    val groupedHistories = remember(histories) {
        histories.groupBy { history ->
            val now = System.currentTimeMillis()
            val diff = now - history.timestamp
            when {
                diff < 86400000 -> "Today"
                diff < 172800000 -> "Yesterday"
                diff < 604800000 -> "This Week"
                else -> "Older"
            }
        }
    }
    
    Column(modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        Text(
            text = "CHAT HISTORY",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        
        if (histories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        tint = colors.textMuted,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No conversations yet",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                groupedHistories.forEach { (group, items) ->
                    item {
                        Text(
                            text = group,
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textMuted,
                            modifier = Modifier.padding(start = 4.dp, top = 12.dp, bottom = 4.dp)
                        )
                    }
                    items(items, key = { it.id }) { history ->
                        HistoryItem(
                            history = history,
                            isSelected = history.id == selectedHistoryId,
                            onClick = { onSelectHistory(history.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(
    history: ChatHistory,
    isSelected: Boolean,
    onClick: () -> Unit,
    onRename: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val colors = UnifiedTheme.colors
    val context = LocalContext.current
    val model = AIModels.allModels.find { it.id == history.modelId }
    val providerColor = model?.brandColor ?: colors.textSecondary
    var showMenu by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) providerColor.copy(alpha = 0.1f) else Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (model != null) {
                    Icon(
                        painter = painterResource(id = model.iconRes),
                        contentDescription = model.name,
                        modifier = Modifier.size(32.dp),
                        tint = Color.Unspecified
                    )
                } else {
                    Text(
                        text = "?",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = providerColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(10.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = history.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) providerColor else colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = history.lastMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        modifier = Modifier.size(16.dp),
                        tint = colors.textMuted
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    containerColor = colors.surfaceElevated
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = colors.textSecondary
                                )
                                Text(
                                    text = "Rename",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.textPrimary
                                )
                            }
                        },
                        onClick = {
                            showMenu = false
                            onRename()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = colors.textSecondary
                                )
                                Text(
                                    text = "Share",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.textPrimary
                                )
                            }
                        },
                        onClick = {
                            showMenu = false
                            // Create shareable link
                            val shareLink = "https://ai.isdev.in/chat/${history.id}"
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Chat: ${history.title}")
                                putExtra(Intent.EXTRA_TEXT, "Open this chat in Aquariusly:\n$shareLink")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Chat"))
                        }
                    )
                    HorizontalDivider(color = colors.borderDefault)
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = colors.error
                                )
                                Text(
                                    text = "Delete",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.error
                                )
                            }
                        },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModelSelectionSection(
    selectedModel: AIModel,
    onModelSelect: (AIModel) -> Unit
) {
    val colors = UnifiedTheme.colors
    var isExpanded by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.padding(12.dp)) {
        Text(
            text = "MODEL",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        
        // Current model selector
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { isExpanded = !isExpanded },
            shape = RoundedCornerShape(12.dp),
            color = colors.surfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.borderDefault)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = selectedModel.iconRes),
                        contentDescription = selectedModel.name,
                        modifier = Modifier.size(32.dp),
                        tint = Color.Unspecified
                    )
                }
                
                Spacer(modifier = Modifier.width(10.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedModel.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = getProviderDisplayName(selectedModel.provider),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted
                    )
                }
                
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp 
                                  else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        // Expanded model list - simple vertical list
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AIModels.allModels.forEach { model ->
                    SimpleModelItem(
                        model = model,
                        isSelected = model.id == selectedModel.id,
                        onClick = {
                            onModelSelect(model)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleModelItem(
    model: AIModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) model.brandColor.copy(alpha = 0.12f) else colors.surfaceElevated,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, model.brandColor.copy(alpha = 0.5f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = model.iconRes),
                    contentDescription = model.name,
                    modifier = Modifier.size(28.dp),
                    tint = Color.Unspecified
                )
            }
            
            Spacer(modifier = Modifier.width(10.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = model.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) model.brandColor else colors.textPrimary
                    )
                    if (model.isPremium) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = colors.accentAmber.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "PRO",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 8.sp,
                                color = colors.accentAmber,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = getProviderDisplayName(model.provider),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    modifier = Modifier.size(18.dp),
                    tint = model.brandColor
                )
            }
        }
    }
}

@Composable
private fun BottomSettingsSection(
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val colors = UnifiedTheme.colors
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Settings Button
        Surface(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable(onClick = onSettingsClick),
            shape = RoundedCornerShape(10.dp),
            color = colors.surfaceElevated
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(18.dp),
                    tint = colors.textSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        }
        
        // Profile Button
        Surface(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable(onClick = onProfileClick),
            shape = RoundedCornerShape(10.dp),
            color = colors.surfaceElevated
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(18.dp),
                    tint = colors.textSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}

private fun getProviderDisplayName(provider: AIProvider): String {
    return when (provider) {
        AIProvider.OPENAI -> "OpenAI"
        AIProvider.ANTHROPIC -> "Anthropic"
        AIProvider.GOOGLE -> "Google"
        AIProvider.XAI -> "xAI"
        AIProvider.DEEPSEEK -> "DeepSeek"
        AIProvider.MISTRAL -> "Mistral AI"
        AIProvider.META -> "Meta"
        AIProvider.ALIBABA -> "Alibaba"
    }
}
